package com.novelreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 配置类，负责加载和管理程序配置参数
 */
public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    
    private static String apiKey;
    private static String inputFilePath;
    private static String outputDirectory;
    private static int rateLimitPerMinute = 15; // 默认每分钟15次API调用
    private static int threadCount = 10; // 默认10个线程
    private static String prompt;
    public static String gptModelName = "gemini-2.0-flash";
    public static String analysisResultsDirPath = Paths.get("output","analysis").toString();
    public static String fileSliceDirPath = Paths.get("output","slices").toString();

    // 静态初始化块，替代原来的构造函数
    static {
        loadConfiguration();
    }
    
    // 私有构造函数，防止实例化
    private Configuration() {}
    
    /**
     * 加载配置信息
     */
    @SuppressWarnings("unchecked")
    private static void loadConfiguration() {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream("apikey.yml")) {
            Map<String, Object> config = yaml.load(inputStream);
            apiKey = (String) config.get("api_key");
            logger.info("API key loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load API key configuration", e);
            throw new RuntimeException("Failed to load API key configuration", e);
        }
        
        // 设置默认值或从命令行参数获取
        inputFilePath = "novel.txt"; // 默认输入文件名
        outputDirectory = "output"; // 默认输出目录
        
        // 加载固定的prompt
        prompt = "请你阅读并逐步分析《牧神记》的每一章节。在分析中，请重点关注以下几个方面，并尽可能提供详细的描写和具体的情节：\n\n" +
                "男主角的经历：\n\n" +
                "男主角在本章节中的冒险旅程、日常生活，以及遇到的具体事件。\n\n" +
                "描述男主角在这些章节中的成长，体现在性格、技能、心境方面的变化。\n\n" +
                "男主角在这些章节中是否有重要的转折点或关键事件？如果有，请详细描述事件的内容和影响。\n\n" +
                "世界观与设定：\n\n" +
                "在本章节中，小说世界观有什么体现？\n\n" +
                "详细解释本章节涉及的地理环境、设定、特殊规则和力量原理\n\n" +
                "人物关系：\n\n" +
                "列出并详细描述与男主角相关的关键人物在本章节中的表现，他们之间的互动、对话，以及对主角的影响。\n" +
                "他们的背景、性格，以及与主角的关系。\n\n" +
                "虚构历史：\n\n" +
                "如果本章节提到重要的历史事件、传说、神话，请详细说明。\n" +
                "这些历史事件和传说对当前世界和男主角产生了什么影响？\n" +
                "其他重要细节：\n\n" +
                "除了上述四点，请关注章节中任何重要的细节，包括伏笔、线索、暗示等。\n\n" +
                "总结每章的关键内容，并分析章节之间的关联性。\n\n" +
                "请注意：\n\n" +
                "避免过于简略，重点突出情节的细节，尽可能用具体的例子和描述来支撑你的分析。\n\n" +
                "对于战斗场景，可以简要概括，但重点要放在战斗的结果、对剧情的影响，以及战斗中体现的人物关系和设定。\n\n" +
                "对伏笔、线索等进行提示，并分析其可能的影响。";
    }
    
    // 静态 Getters
    public static String getApiKey() {
        return apiKey;
    }
    
    public static String getInputFilePath() {
        return inputFilePath;
    }
    
    public static String getOutputDirectory() {
        return outputDirectory;
    }
    
    public static int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }
    
    public static int getThreadCount() {
        return threadCount;
    }
    
    public static String getPrompt() {
        return prompt;
    }
    
    // 静态 Setters
    public static void setInputFilePath(String path) {
        inputFilePath = path;
    }
    
    public static void setOutputDirectory(String directory) {
        outputDirectory = directory;
    }
}
