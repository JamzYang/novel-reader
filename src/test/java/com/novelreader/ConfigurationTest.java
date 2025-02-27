package com.novelreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 配置类的单元测试
 */
public class ConfigurationTest {
    
    @TempDir
    Path tempDir;
    
    private File apiKeyFile;
    private final String testApiKey = "test_api_key_12345";
    
    @BeforeEach
    public void setUp() throws IOException {
        // 创建测试API密钥文件
        apiKeyFile = new File(tempDir.toFile(), "apikey.yml");
        try (FileWriter writer = new FileWriter(apiKeyFile)) {
            writer.write("# gemini api key\napi_key: " + testApiKey);
        }
    }
    
    /**
     * 注意：这个测试方法需要修改当前工作目录或者修改Configuration类以接受文件路径参数
     * 在实际项目中，你可能需要重构Configuration类以便于测试
     */
    @Test
    public void testLoadConfiguration() {
        // 由于Configuration类直接从固定路径加载apikey.yml，
        // 这个测试在没有实际文件的情况下会失败
        // 这里我们只测试基本的getter和setter方法
        
        Configuration config = new Configuration();
        
        // 测试默认值
        assertEquals("novel.txt", config.getInputFilePath(), "默认输入文件路径不正确");
        assertEquals("output", config.getOutputDirectory(), "默认输出目录不正确");
        assertEquals(15, config.getRateLimitPerMinute(), "默认限流值不正确");
        assertEquals(10, config.getThreadCount(), "默认线程数不正确");
        assertNotNull(config.getPrompt(), "提示不应为空");
        
        // 测试setter
        String newInputPath = "new_novel.txt";
        String newOutputDir = "new_output";
        
        config.setInputFilePath(newInputPath);
        config.setOutputDirectory(newOutputDir);
        
        assertEquals(newInputPath, config.getInputFilePath(), "更新后的输入文件路径不正确");
        assertEquals(newOutputDir, config.getOutputDirectory(), "更新后的输出目录不正确");
    }
    
    @Test
    public void testPromptContent() {
        Configuration config = new Configuration();
        String prompt = config.getPrompt();
        
        // 验证提示内容包含所有必要的部分
        assertTrue(prompt.contains("男主角的经历"), "提示应包含'男主角的经历'部分");
        assertTrue(prompt.contains("世界观与设定"), "提示应包含'世界观与设定'部分");
        assertTrue(prompt.contains("人物关系"), "提示应包含'人物关系'部分");
        assertTrue(prompt.contains("虚构历史"), "提示应包含'虚构历史'部分");
        assertTrue(prompt.contains("其他重要细节"), "提示应包含'其他重要细节'部分");
    }
}
