package com.novelreader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultSaver的单元测试
 */
public class ResultSaverTest {
    
    private ResultSaver resultSaver;
    private Gson gson;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setUp() {
        resultSaver = new ResultSaver();
        gson = new Gson();
    }
    
    @Test
    public void testSaveChapterGroupResult() throws IOException {
        // 准备测试数据
        ChapterGroup group1 = new ChapterGroup(1, 1, 2);
        group1.addChapter(new Chapter(1, "第1章", "测试内容1-1"));
        group1.addChapter(new Chapter(2, "第2章", "测试内容1-2"));
        String markdownResult = """
                好的，我将按照您提出的要求，对《牧神记》的第一章和第二章进行详细分析。
                
                **第1章 天黑别出门**
                
                * **男主角的经历：**
                  * 秦牧在本章中还是一个婴儿，被司婆婆在江边捡到，由残老村的村民抚养。
                
                **第2章 四灵血**
                
                * **男主角的经历：**
                  * 秦牧在本章中接受了四灵血的测试，试图激发体内的灵体。
                """;
        
        // 执行保存
        String outputPath = tempDir.toString();
        String resultFilePath = resultSaver.saveChapterGroupResult(group1, markdownResult, outputPath);
        
        // 断言结果
        assertNotNull(resultFilePath, "保存结果应返回文件路径");
        assertTrue(Files.exists(Paths.get(resultFilePath)), "结果文件应该存在");
        
        // 验证文件内容
        String fileContent = new String(Files.readAllBytes(Paths.get(resultFilePath)));
        JsonObject resultJson = gson.fromJson(fileContent, JsonObject.class);
        
        assertEquals(1, resultJson.get("chapterGroupId").getAsInt(), "章节组ID应匹配");
        assertEquals(1, resultJson.get("startChapter").getAsInt(), "起始章节应匹配");
        assertEquals(2, resultJson.get("endChapter").getAsInt(), "结束章节应匹配");
        assertEquals(markdownResult, resultJson.get("markdownResult").getAsString(), "Markdown结果应匹配");
    }
    
    @Test
    public void testMergeResults() throws IOException {
        // 准备测试数据
        // 创建第一个测试文件
        ChapterGroup group1 = new ChapterGroup(1, 1, 2);
        group1.addChapter(new Chapter(1, "第1章", "测试内容1-1"));
        group1.addChapter(new Chapter(2, "第2章", "测试内容1-2"));
        String markdownResult1 = """
                **第1章 天黑别出门**
                
                * **男主角的经历：**
                  * 秦牧在本章中还是一个婴儿，被司婆婆在江边捡到，由残老村的村民抚养。
                
                * **世界观与设定：**
                  * 黑暗的设定：本章最核心的设定是"天黑别出门"。
                
                * **人物关系：**
                  * 司婆婆：捡到秦牧的人，对秦牧非常疼爱。
                
                * **虚构历史：**
                  * 本章没有明确提及具体的历史事件或传说。
                
                * **其他重要细节：**
                  * 玉佩：玉佩上的"秦"字暗示了秦牧的姓氏。
                
                * **总结：**
                  * 本章主要介绍了秦牧的身世和残老村的特殊环境。
                
                **第2章 四灵血**
                
                * **男主角的经历：**
                  * 秦牧在本章中接受了四灵血的测试，试图激发体内的灵体。
                
                * **世界观与设定：**
                  * 灵体的设定：本章详细介绍了灵体的概念。
                
                * **人物关系：**
                  * 村长：在秦牧的成长中扮演了重要的引导者角色。
                
                * **虚构历史：**
                  * 本章没有明确提及具体的历史事件或传说。
                
                * **其他重要细节：**
                  * 四灵血的炼制过程：展示了残老村村民独特的炼药技巧。
                
                * **总结：**
                  * 本章主要围绕秦牧的体质测试展开。
                """;
        
        // 创建第二个测试文件
        ChapterGroup group2 = new ChapterGroup(2, 3, 4);
        group1.addChapter(new Chapter(3, "第3章", "测试内容3-1"));
        group1.addChapter(new Chapter(4, "第4章", "测试内容4-2"));
        String markdownResult2 = """
                **第3章 修行开始**
                
                * **男主角的经历：**
                  * 秦牧开始了他的修行之路。
                
                * **世界观与设定：**
                  * 修行体系：本章介绍了修行的基本概念。
                
                * **人物关系：**
                  * 药师：教导秦牧炼药的技巧。
                
                * **虚构历史：**
                  * 本章提到了一些古老的修行传说。
                
                * **其他重要细节：**
                  * 秦牧的天赋：虽然是普通体质，但学习能力惊人。
                
                * **总结：**
                  * 本章描述了秦牧修行的起步阶段。
                
                **第4章 初次历练**
                
                * **男主角的经历：**
                  * 秦牧第一次离开村子进行历练。
                
                * **世界观与设定：**
                  * 大墟：本章详细描述了大墟的危险环境。
                
                * **人物关系：**
                  * 瘸子爷爷：陪伴秦牧历练，保护他的安全。
                
                * **虚构历史：**
                  * 本章提到了大墟的形成历史。
                
                * **其他重要细节：**
                  * 秦牧的成长：初次历练让他更加坚定了修行的决心。
                
                * **总结：**
                  * 本章展示了秦牧面对危险时的勇气和智慧。
                """;
        
        // 保存测试文件
        String outputPath = tempDir.toString();
        String resultFilePath1 = resultSaver.saveChapterGroupResult(group1, markdownResult1, outputPath);
        String resultFilePath2 = resultSaver.saveChapterGroupResult(group2, markdownResult2, outputPath);
        
        // 执行合并
        String mergedFilePath = Paths.get(outputPath, "merged_results.json").toString();
        boolean mergeResult = resultSaver.mergeResults(Arrays.asList(resultFilePath1, resultFilePath2), mergedFilePath);
        
        // 断言结果
        assertTrue(mergeResult, "合并结果应返回true");
        assertTrue(Files.exists(Paths.get(mergedFilePath)), "合并后的文件应该存在");
        
        // 验证合并后的文件内容
        String fileContent = new String(Files.readAllBytes(Paths.get(mergedFilePath)));
        JsonObject resultJson = gson.fromJson(fileContent, JsonObject.class);
        
        // 验证分析结果数组
        JsonArray analysisResults = resultJson.getAsJsonArray("analysis_results");
        assertNotNull(analysisResults, "应包含analysis_results数组");
        assertEquals(4, analysisResults.size(), "应包含4个章节的分析结果");
        
        // 验证第一个章节的内容
        JsonObject chapter1 = analysisResults.get(0).getAsJsonObject();
        assertEquals(1, chapter1.get("chapter_number").getAsInt(), "第一个章节的编号应为1");
        assertEquals("天黑别出门", chapter1.get("chapter_title").getAsString(), "第一个章节的标题应匹配");
        assertTrue(chapter1.has("protagonist_experience"), "应包含男主角经历");
        assertTrue(chapter1.has("world_setting"), "应包含世界观与设定");
        assertTrue(chapter1.has("character_relationships"), "应包含人物关系");
        assertTrue(chapter1.has("fictional_history"), "应包含虚构历史");
        assertTrue(chapter1.has("important_details"), "应包含其他重要细节");
        assertTrue(chapter1.has("summary"), "应包含总结");
    }
    
    @Test
    public void testMergeResults_WithEmptyFileList() {
        // 准备测试数据
        List<String> emptyFileList = List.of();
        String mergedFilePath = Paths.get(tempDir.toString(), "empty_merged_results.json").toString();
        
        // 执行合并
        boolean mergeResult = resultSaver.mergeResults(emptyFileList, mergedFilePath);
        
        // 断言结果
        assertFalse(mergeResult, "文件列表为空，合并失败");

        // 验证合并后的文件内容
//        try {
//            String fileContent = new String(Files.readAllBytes(Paths.get(mergedFilePath)));
//            JsonObject resultJson = gson.fromJson(fileContent, JsonObject.class);
//
//            JsonArray analysisResults = resultJson.getAsJsonArray("analysis_results");
//            assertNotNull(analysisResults, "应包含analysis_results数组");
//            assertEquals(0, analysisResults.size(), "数组应为空");
//        } catch (IOException e) {
//            fail("读取合并文件失败: " + e.getMessage());
//        }
    }
}
