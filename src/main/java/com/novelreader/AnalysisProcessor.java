package com.novelreader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分析处理器，用于处理小说分析的整个流程
 */
public class AnalysisProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisProcessor.class);
    private static final int MAX_RETRY_COUNT = 3;
    private static final int THREAD_COUNT = 10;
    
    private final ApiClient apiClient;
    private final ResultValidator validator;
    private final ResultSaver resultSaver;
    private final String outputDirectory;
    private final String finalOutputFile;
    
    /**
     * 构造函数
     * 
     * @param apiClient API客户端
     * @param validator 结果验证器
     * @param resultSaver 结果保存器
     * @param outputDirectory 输出目录
     * @param finalOutputFile 最终输出文件路径
     */
    public AnalysisProcessor(ApiClient apiClient, ResultValidator validator, ResultSaver resultSaver, 
                             String outputDirectory, String finalOutputFile) {
        this.apiClient = apiClient;
        this.validator = validator;
        this.resultSaver = resultSaver;
        this.outputDirectory = outputDirectory;
        this.finalOutputFile = finalOutputFile;
        
        // 确保输出目录存在
        File directory = new File(outputDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * 处理章节组列表
     * 
     * @param chapterGroups 章节组列表
     * @return 是否成功处理所有章节组
     */
    public boolean processChapterGroups(List<ChapterGroup> chapterGroups) {
        logger.info("开始处理{}个章节组", chapterGroups.size());
        try {
            List<Path> analysisPaths = Files.list(Paths.get(Configuration.analysisResultsDirPath))
                                           .collect(Collectors.toList());
            chapterGroups = chapterGroups.stream()
                .filter(it ->
                    analysisPaths.stream().noneMatch(p ->
                        p.getFileName().toString().equals(it.getAnalysisFileName())
                    )
                )
                .collect(Collectors.toList());
        }catch (Exception e) {
            logger.warn("过滤已分析文件",e);
        }

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<CompletableFuture<String>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        
        // 提交任务
        for (ChapterGroup chapterGroup : chapterGroups) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return processChapterGroup(chapterGroup);
                } catch (Exception e) {
                    logger.error("处理章节组{}时发生错误: {}", chapterGroup.getChapterGroupId(), e.getMessage(), e);
                    return null;
                }
            }, executor);
            
            futures.add(future);
        }
        
        // 收集结果
        List<String> resultFiles = new ArrayList<>();
        for (CompletableFuture<String> future : futures) {
            try {
                String resultFile = future.get();
                if (resultFile != null) {
                    resultFiles.add(resultFile);
                    successCount.incrementAndGet();
                }
            } catch (Exception e) {
                logger.error("获取任务结果时发生错误: {}", e.getMessage(), e);
            }
        }
        
        // 关闭线程池
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("成功处理了{}/{}个章节组", successCount.get(), chapterGroups.size());
        
        // 合并结果
        if (!resultFiles.isEmpty()) {
            boolean mergeResult = resultSaver.mergeResults(resultFiles, finalOutputFile);
            if (mergeResult) {
                logger.info("成功合并所有分析结果到: {}", finalOutputFile);
                return true;
            } else {
                logger.error("合并分析结果失败");
                return false;
            }
        } else {
            logger.error("没有成功处理的章节组，无法合并结果");
            return false;
        }
    }
    
    /**
     * 处理单个章节组
     * 
     * @param chapterGroup 章节组
     * @return 保存的结果文件路径，如果处理失败则返回null
     */
    private String processChapterGroup(ChapterGroup chapterGroup) {
        logger.info("开始处理章节组: {}（第{}章-第{}章）", 
                chapterGroup.getChapterGroupId(), chapterGroup.getStartChapterNumber(), chapterGroup.getEndChapterNumber());
        
        // 计算预期章节数量
        int expectedChapterCount = chapterGroup.getEndChapterNumber() - chapterGroup.getStartChapterNumber() + 1;
        
        // 创建API请求
        ApiRequest request = new ApiRequest(Configuration.getPrompt(),chapterGroup.getContent());
        int retryCount = 0;
        // 尝试调用API并验证结果
        for (; retryCount <= MAX_RETRY_COUNT; retryCount++) {
            if (retryCount > 0) {
                logger.info("第{}次重试处理章节组: {}", retryCount, chapterGroup.getChapterGroupId());
            }
            
            // 调用API
            ApiResponse response = apiClient.analyzeChapterGroup(request);
            
            // 检查API调用是否成功
            if (!response.isSuccess()) {
                logger.error("API调用失败: {}", response.errorMessage());
                if (retryCount < MAX_RETRY_COUNT) {
                    continue;
                } else {
                    logger.error("达到最大重试次数，放弃处理章节组: {}", chapterGroup.getChapterGroupId());
                    return null;
                }
            }
            
            // 验证结果
            String markdownResult = response.responseBody();
            boolean isValid = validator.validateResult(markdownResult, expectedChapterCount);
            
            if (isValid) {
                logger.info("章节组{}验证通过", chapterGroup.getChapterGroupId());
                
                // 保存结果
                String resultFilePath = resultSaver.saveChapterGroupResult(chapterGroup, markdownResult, outputDirectory);
                if (resultFilePath != null) {
                    logger.info("成功保存章节组{}的分析结果: {}", chapterGroup.getChapterGroupId(), resultFilePath);
                    return resultFilePath;
                } else {
                    logger.error("保存章节组{}的分析结果失败", chapterGroup.getChapterGroupId());
                    return null;
                }
            } else {
                logger.warn("章节组{}验证失败: 预期章节数量{}, 实际解析出的章节数量不匹配", 
                        chapterGroup.getChapterGroupId(), expectedChapterCount);
                logger.warn("GPT API返回内容: {}",markdownResult);
                
                if (retryCount < MAX_RETRY_COUNT) {
                    logger.info("将进行第{}次重试", retryCount + 1);
                } else {
                    logger.error("达到最大重试次数，放弃处理章节组: {}", chapterGroup.getChapterGroupId());
                    return null;
                }
            }
        }
        
        return null;
    }
}
