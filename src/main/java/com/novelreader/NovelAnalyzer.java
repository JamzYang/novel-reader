package com.novelreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.List;

/**
 * 小说分析器，程序的主入口
 */
public class NovelAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(NovelAnalyzer.class);
    
    public static void main(String[] args) {
        String novelFilePath = Paths.get("牧神记.txt").toString();
        String outputDirectory = Paths.get("output").toString();
        
        try {

            logger.info("开始分析小说: {}", novelFilePath);
            
            // 1. 识别章节
            logger.info("步骤1: 识别章节");
            ChapterIdentifier chapterIdentifier = new ChapterIdentifier();
            List<Chapter> chapters = chapterIdentifier.identifyChapters(novelFilePath);
            
            // 2. 验证章节顺序
            logger.info("步骤2: 验证章节顺序");
            ChapterValidator chapterValidator = new ChapterValidator();
            ValidationResult validationResult = chapterValidator.validateChapterOrder(chapters);
            
            if (!validationResult.isValid()) {
                logger.error("章节验证失败: {}", validationResult.getErrorMessage());
                System.exit(1);
            }
            
            // 3. 分割小说
            logger.info("步骤3: 分割小说");
            ChapterSplitter chapterSplitter = new ChapterSplitter();
            List<ChapterGroup> chapterGroups = chapterSplitter.splitNovelIntoChapterGroups(chapters, outputDirectory);
            
            // 4 & 5. 分析小说并保存结果
            logger.info("步骤4 & 5: 分析小说并保存结果");
            String apiKey = Configuration.getApiKey();
            if (apiKey == null || apiKey.isEmpty()) {
                logger.error("未配置API密钥，请在config.properties中设置api.key");
                System.exit(1);
            }
            
            // 创建API客户端
            GeminiApiClient apiClient = new GeminiApiClient(apiKey, new RateLimiter(15));
            
            // 创建结果验证器和保存器
            ResultValidator validator = new ResultValidator();
            ResultSaver resultSaver = new ResultSaver();
            
            // 创建分析处理器
            String finalOutputFile = Paths.get(Configuration.analysisResultsDirPath, "novel_analysis_results.json").toString();
            AnalysisProcessor processor = new AnalysisProcessor(
                    apiClient, validator, resultSaver, Configuration.analysisResultsDirPath, finalOutputFile);
            
            // 处理章节组
            boolean success = processor.processChapterGroups(chapterGroups);
            
            if (success) {
                logger.info("小说分析完成，结果保存在: {}", finalOutputFile);
            } else {
                logger.error("小说分析过程中发生错误");
                System.exit(1);
            }
            
        } catch (Exception e) {
            logger.error("程序执行过程中发生错误: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
