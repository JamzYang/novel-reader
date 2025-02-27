package com.novelreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 章节校验器的单元测试
 */
public class ChapterValidatorTest {
    
    private ChapterValidator chapterValidator;
    private List<Chapter> validChapters;
    
    @BeforeEach
    public void setUp() {
        chapterValidator = new ChapterValidator();
        
        // 创建有效的章节列表
        validChapters = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            validChapters.add(new Chapter(i, "第" + i + "章 测试", "这是第" + i + "章的内容"));
        }
    }
    
    @Test
    public void testValidateChapterOrderWithValidChapters() {
        // 测试有效的章节列表
        ValidationResult result = chapterValidator.validateChapterOrder(validChapters);
        
        assertTrue(result.isValid(), "有效章节列表应通过验证");
        assertNull(result.getErrorMessage(), "有效章节列表不应有错误消息");
    }
    
    @Test
    public void testValidateChapterOrderWithEmptyChapters() {
        // 测试空章节列表
        ValidationResult result = chapterValidator.validateChapterOrder(new ArrayList<>());
        
        assertFalse(result.isValid(), "空章节列表不应通过验证");
        assertEquals("No chapters found", result.getErrorMessage(), "错误消息不正确");
    }
    
    @Test
    public void testValidateChapterOrderWithDuplicateChapters() {
        // 创建包含重复章节的列表
        List<Chapter> duplicateChapters = new ArrayList<>(validChapters);
        duplicateChapters.add(new Chapter(5, "第5章 重复", "这是重复的第5章内容"));
        
        ValidationResult result = chapterValidator.validateChapterOrder(duplicateChapters);
        
        assertFalse(result.isValid(), "包含重复章节的列表不应通过验证");
        assertTrue(result.getErrorMessage().contains("Duplicate chapter found"), "错误消息不正确");
    }
    
    @Test
    public void testValidateChapterOrderWithMissingChapters() {
        // 创建缺少章节的列表
        List<Chapter> missingChapters = new ArrayList<>();
        missingChapters.add(new Chapter(1, "第1章", "内容1"));
        missingChapters.add(new Chapter(2, "第2章", "内容2"));
        missingChapters.add(new Chapter(4, "第4章", "内容4")); // 缺少第3章
        
        ValidationResult result = chapterValidator.validateChapterOrder(missingChapters);
        
        assertFalse(result.isValid(), "缺少章节的列表不应通过验证");
        assertTrue(result.getErrorMessage().contains("Chapter sequence error"), "错误消息不正确");
    }
    
    @Test
    public void testValidateChapterOrderWithOutOfOrderChapters() {
        // 创建章节顺序错误的列表
        List<Chapter> outOfOrderChapters = new ArrayList<>();
        outOfOrderChapters.add(new Chapter(1, "第1章", "内容1"));
        outOfOrderChapters.add(new Chapter(3, "第3章", "内容3")); // 顺序错误
        outOfOrderChapters.add(new Chapter(2, "第2章", "内容2"));
        
        ValidationResult result = chapterValidator.validateChapterOrder(outOfOrderChapters);
        
        assertFalse(result.isValid(), "章节顺序错误的列表不应通过验证");
        assertTrue(result.getErrorMessage().contains("Chapter sequence error"), "错误消息不正确");
    }
}
