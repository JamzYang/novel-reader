package com.novelreader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 结果保存器，用于保存和合并Gemini API的分析结果
 */
public class ResultSaver {
    private static final Logger logger = LoggerFactory.getLogger(ResultSaver.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    // 用于匹配Markdown中的章节标题和内容的正则表达式
    private static final Pattern CHAPTER_PATTERN = Pattern.compile("\\*\\*第(\\d+)章\\s+([^*]+)\\*\\*\\s*([\\s\\S]*?)(?=\\*\\*第\\d+章|$)");
    
    /**
     * 保存单个章节组的分析结果为JSON文件
     * 
     * @param chapterGroup 章节组
     * @param markdownResult Gemini API返回的Markdown结果
     * @param outputDirectory 输出目录
     * @return 保存的JSON文件路径
     */
    public String saveChapterGroupResult(ChapterGroup chapterGroup, String markdownResult, String outputDirectory) {
        try {
            // 创建JSON对象
            JsonObject resultJson = new JsonObject();
            resultJson.addProperty("chapterGroupId", chapterGroup.getChapterGroupId());
            resultJson.addProperty("startChapter", chapterGroup.getStartChapterNumber());
            resultJson.addProperty("endChapter", chapterGroup.getEndChapterNumber());
            resultJson.addProperty("markdownResult", markdownResult);
            
            // 构建文件名
            String fileName = String.format("%03d第%d-%d章_分析.json", 
                    chapterGroup.getChapterGroupId(),
                    chapterGroup.getStartChapterNumber(),
                    chapterGroup.getEndChapterNumber());
            
            String filePath = Paths.get(outputDirectory, fileName).toString();
            
            // 确保输出目录存在
            File directory = new File(outputDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // 写入JSON文件
            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(resultJson, writer);
            }
            
            logger.info("已保存章节组分析结果: {}", filePath);
            return filePath;
        } catch (IOException e) {
            logger.error("保存章节组分析结果失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 合并所有章节组的分析结果到一个markdown文件
     * 
     * @param outputFile 输出文件路径
     * @return 是否成功合并
     */
    public boolean mergeResults(String outputFile) {
        Path analysisResultPath = Paths.get(Configuration.analysisResultsDirPath);
        try {
            // 获取目录中的所有JSON文件
            List<Path> resultFiles = Files.list(analysisResultPath)
                    .filter(path -> path.toString().endsWith(".json"))
                    .sorted() // 按文件名自然排序
                    .collect(Collectors.toList());
            
            // 创建StringBuilder来构建最终的markdown内容
            StringBuilder markdownContent = new StringBuilder();
            
            // 处理每个结果文件
            for (Path filePath : resultFiles) {
                try {
                    // 从文件名中提取标题（例如：从"001第1-10章_分析.json"提取"第1-10章"）
                    String fileName = filePath.getFileName().toString();
                    String titlePart = fileName.replaceAll("^\\d+第(.*?)_分析\\.json$", "$1");
                    
                    // 添加二级标题
                    markdownContent.append("## ").append(titlePart).append("\n\n");
                    
                    // 读取并解析JSON文件
                    String content = new String(Files.readAllBytes(filePath));
                    JsonObject resultJson = gson.fromJson(content, JsonObject.class);
                    
                    // 获取markdownResult字段
                    if (resultJson.has("markdownResult")) {
                        String markdownResultStr = resultJson.get("markdownResult").getAsString();
                        
                        // 解析markdownResult中的JSON以获取text字段
                        JsonObject markdownResultJson = gson.fromJson(markdownResultStr, JsonObject.class);
                        if (markdownResultJson.has("candidates") && 
                            markdownResultJson.getAsJsonArray("candidates").size() > 0) {
                            
                            JsonObject candidate = markdownResultJson.getAsJsonArray("candidates").get(0).getAsJsonObject();
                            if (candidate.has("content") && 
                                candidate.getAsJsonObject("content").has("parts") &&
                                candidate.getAsJsonObject("content").getAsJsonArray("parts").size() > 0) {
                                
                                JsonObject part = candidate.getAsJsonObject("content")
                                                         .getAsJsonArray("parts").get(0).getAsJsonObject();
                                
                                if (part.has("text")) {
                                    // 获取text字段的内容并添加到markdown内容中
                                    String text = part.get("text").getAsString();
                                    markdownContent.append(text).append("\n\n");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("处理结果文件失败: {}, 错误: {}", filePath, e.getMessage(), e);
                }
            }
            
            // 写入最终markdown文件
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(markdownContent.toString());
            }
            
            logger.info("已合并所有分析结果到: {}", outputFile);
            return true;
        } catch (IOException e) {
            logger.error("合并分析结果失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 从Markdown结果中解析出每个章节的分析内容
     * 
     * @param markdownResult Markdown结果
     * @return 章节分析的JSON对象列表
     */
    private List<JsonObject> parseChaptersFromMarkdown(String markdownResult) {
        List<JsonObject> chapterAnalyses = new ArrayList<>();
        Matcher matcher = CHAPTER_PATTERN.matcher(markdownResult);
        
        while (matcher.find()) {
            String chapterNumber = matcher.group(1);
            String chapterTitle = matcher.group(2).trim();
            String chapterContent = matcher.group(3).trim();
            
            JsonObject chapterAnalysis = new JsonObject();
            chapterAnalysis.addProperty("chapter_number", Integer.parseInt(chapterNumber));
            chapterAnalysis.addProperty("chapter_title", chapterTitle);
            
            // 解析章节内容中的各个部分
            JsonObject analysisContent = parseAnalysisContent(chapterContent);
            
            // 将解析结果添加到章节分析中
            for (String key : analysisContent.keySet()) {
                chapterAnalysis.add(key, analysisContent.get(key));
            }
            
            chapterAnalyses.add(chapterAnalysis);
        }
        
        return chapterAnalyses;
    }
    
    /**
     * 解析章节分析内容中的各个部分
     * 
     * @param content 章节分析内容
     * @return 解析后的JSON对象
     */
    private JsonObject parseAnalysisContent(String content) {
        JsonObject result = new JsonObject();
        
        // 提取男主角经历
        extractSection(content, "男主角的经历", "protagonist_experience", result);
        
        // 提取世界观与设定
        extractSection(content, "世界观与设定", "world_setting", result);
        
        // 提取人物关系
        extractSection(content, "人物关系", "character_relationships", result);
        
        // 提取虚构历史
        extractSection(content, "虚构历史", "fictional_history", result);
        
        // 提取其他重要细节
        extractSection(content, "其他重要细节", "important_details", result);
        
        // 提取总结
        extractSection(content, "总结", "summary", result);
        
        return result;
    }
    
    /**
     * 从内容中提取特定部分
     * 
     * @param content 内容
     * @param sectionName 部分名称
     * @param jsonKey JSON键名
     * @param result 结果JSON对象
     */
    private void extractSection(String content, String sectionName, String jsonKey, JsonObject result) {
        Pattern pattern = Pattern.compile("\\*\\*" + sectionName + "：?\\*\\*\\s*([\\s\\S]*?)(?=\\*\\*\\w+：?\\*\\*|$)");
        Matcher matcher = pattern.matcher(content);
        
        if (matcher.find()) {
            String sectionContent = matcher.group(1).trim();
            result.addProperty(jsonKey, sectionContent);
        } else {
            result.addProperty(jsonKey, "");
        }
    }
}
