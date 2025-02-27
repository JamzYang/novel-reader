package com.novelreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 章节识别器的单元测试
 */
public class ChapterIdentifierTest {
    
    private ChapterIdentifier chapterIdentifier;
    private String testNovelPath;
    
    @BeforeEach
    public void setUp() {
        chapterIdentifier = new ChapterIdentifier();
        Path resourceDirectory = Paths.get("src", "test", "resources", "牧神记_test.txt");
        testNovelPath = resourceDirectory.toAbsolutePath().toString();
    }
    
    @Test
    public void testIdentifyChapters() {
        // 执行章节识别
        List<Chapter> chapters = chapterIdentifier.identifyChapters(testNovelPath);
        
        // 验证结果
        assertNotNull(chapters, "章节列表不应为空");
        assertEquals(20, chapters.size(), "应识别出20个章节");
        
        // 验证第一章
        Chapter firstChapter = chapters.get(0);
        assertEquals(1, firstChapter.getChapterNumber(), "第一章的章节号应为1");
        assertEquals("第1章 少年", firstChapter.getChapterTitle(), "第一章标题不正确");
        assertTrue(firstChapter.getChapterContent().contains("清晨，阳光照在青石板路上"), "第一章内容不正确");
        
        // 验证第十章
        Chapter tenthChapter = chapters.get(9);
        assertEquals(10, tenthChapter.getChapterNumber(), "第十章的章节号应为10");
        assertEquals("第10章 真相", tenthChapter.getChapterTitle(), "第十章标题不正确");
        assertTrue(tenthChapter.getChapterContent().contains("长老告诉陈长生"), "第十章内容不正确");
        
        // 验证最后一章
        Chapter lastChapter = chapters.get(chapters.size() - 1);
        assertEquals(20, lastChapter.getChapterNumber(), "最后一章的章节号应为20");
        assertEquals("第20章 重生", lastChapter.getChapterTitle(), "最后一章标题不正确");
        assertTrue(lastChapter.getChapterContent().contains("这不是终点，而是新的开始"), "最后一章内容不正确");
    }
    
    @Test
    public void testIdentifyChaptersWithNonExistentFile() {
        // 测试不存在的文件
        Exception exception = assertThrows(RuntimeException.class, () -> {
            chapterIdentifier.identifyChapters("non_existent_file.txt");
        });
        
        assertTrue(exception.getMessage().contains("Error reading novel file"), "异常消息不正确");
    }
}
