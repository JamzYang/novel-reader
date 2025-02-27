package com.novelreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 章节分割器的单元测试
 */
public class ChapterSplitterTest {
    
    private ChapterSplitter chapterSplitter;
    private List<Chapter> testChapters;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    public void setUp() {
        chapterSplitter = new ChapterSplitter();
        
        // 创建测试章节
        testChapters = new ArrayList<>();
        for (int i = 1; i <= 25; i++) {
            testChapters.add(new Chapter(i, "第" + i + "章 测试", "这是第" + i + "章的内容，用于测试章节分割功能。"));
        }
    }
    
    @Test
    public void testSplitNovelIntoChapterGroups() {
        // 执行章节分割
        String outputDir = tempDir.toString();
        List<ChapterGroup> chapterGroups = chapterSplitter.splitNovelIntoChapterGroups(testChapters, outputDir);
        
        // 验证结果
        assertNotNull(chapterGroups, "章节组列表不应为空");
        assertEquals(3, chapterGroups.size(), "应该生成3个章节组");
        
        // 验证第一个章节组
        ChapterGroup firstGroup = chapterGroups.get(0);
        assertEquals(1, firstGroup.getChapterGroupId(), "第一个章节组ID应为1");
        assertEquals(1, firstGroup.getStartChapterNumber(), "第一个章节组起始章节号应为1");
        assertEquals(10, firstGroup.getEndChapterNumber(), "第一个章节组结束章节号应为10");
        assertEquals(10, firstGroup.getChapterCount(), "第一个章节组应包含10个章节");
        
        // 验证第二个章节组
        ChapterGroup secondGroup = chapterGroups.get(1);
        assertEquals(2, secondGroup.getChapterGroupId(), "第二个章节组ID应为2");
        assertEquals(11, secondGroup.getStartChapterNumber(), "第二个章节组起始章节号应为11");
        assertEquals(20, secondGroup.getEndChapterNumber(), "第二个章节组结束章节号应为20");
        assertEquals(10, secondGroup.getChapterCount(), "第二个章节组应包含10个章节");
        
        // 验证第三个章节组
        ChapterGroup thirdGroup = chapterGroups.get(2);
        assertEquals(3, thirdGroup.getChapterGroupId(), "第三个章节组ID应为3");
        assertEquals(21, thirdGroup.getStartChapterNumber(), "第三个章节组起始章节号应为21");
        assertEquals(25, thirdGroup.getEndChapterNumber(), "第三个章节组结束章节号应为25");
        assertEquals(5, thirdGroup.getChapterCount(), "第三个章节组应包含5个章节");
        
        // 验证文件是否生成
        File firstFile = new File(outputDir, firstGroup.getFileName());
        File secondFile = new File(outputDir, secondGroup.getFileName());
        File thirdFile = new File(outputDir, thirdGroup.getFileName());
        
        assertTrue(firstFile.exists(), "第一个章节组文件应该存在");
        assertTrue(secondFile.exists(), "第二个章节组文件应该存在");
        assertTrue(thirdFile.exists(), "第三个章节组文件应该存在");
    }
    
    @Test
    public void testWriteChapterGroupToFile() throws IOException {
        // 创建测试章节组
        ChapterGroup testGroup = new ChapterGroup(1, 1, 3);
        testGroup.addChapter(new Chapter(1, "第1章 测试", "这是第1章的内容"));
        testGroup.addChapter(new Chapter(2, "第2章 测试", "这是第2章的内容"));
        testGroup.addChapter(new Chapter(3, "第3章 测试", "这是第3章的内容"));
        
        // 执行写入
        String outputDir = tempDir.toString();
        chapterSplitter.writeChapterGroupToFile(testGroup);
        
        // 验证文件是否生成
        File outputFile = new File(outputDir, testGroup.getFileName());
        assertTrue(outputFile.exists(), "输出文件应该存在");
        
        // 验证文件内容
        String fileContent = Files.readString(outputFile.toPath());
        assertTrue(fileContent.contains("第1章 测试"), "文件内容应包含第1章标题");
        assertTrue(fileContent.contains("这是第1章的内容"), "文件内容应包含第1章内容");
        assertTrue(fileContent.contains("第2章 测试"), "文件内容应包含第2章标题");
        assertTrue(fileContent.contains("这是第2章的内容"), "文件内容应包含第2章内容");
        assertTrue(fileContent.contains("第3章 测试"), "文件内容应包含第3章标题");
        assertTrue(fileContent.contains("这是第3章的内容"), "文件内容应包含第3章内容");
    }
}
