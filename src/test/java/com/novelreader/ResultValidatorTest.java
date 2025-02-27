package com.novelreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultValidator的单元测试
 */
public class ResultValidatorTest {
    
    private ResultValidator validator;
    
    @BeforeEach
    public void setUp() {
        validator = new ResultValidator();
    }
    
    @Test
    public void testValidateResult_WithCorrectChapterCount() {
        // 准备测试数据
        String markdownResult = """
                好的，我将按照您提出的要求，对《牧神记》的第一章和第二章进行详细分析。
                
                **第1章 天黑别出门**
                
                * **男主角的经历：**
                  * 秦牧在本章中还是一个婴儿，被司婆婆在江边捡到，由残老村的村民抚养。
                
                **第2章 四灵血**
                
                * **男主角的经历：**
                  * 秦牧在本章中接受了四灵血的测试，试图激发体内的灵体。
                """;
        
        // 执行验证
        boolean result = validator.validateResult(markdownResult, 2);
        
        // 断言结果
        assertTrue(result, "章节数量正确时应返回true");
    }
    
    @Test
    public void testValidateResult_WithIncorrectChapterCount() {
        // 准备测试数据
        String markdownResult = """
                好的，我将按照您提出的要求，对《牧神记》的第一章进行详细分析。
                
                **第1章 天黑别出门**
                
                * **男主角的经历：**
                  * 秦牧在本章中还是一个婴儿，被司婆婆在江边捡到，由残老村的村民抚养。
                """;
        
        // 执行验证
        boolean result = validator.validateResult(markdownResult, 2);
        
        // 断言结果
        assertFalse(result, "章节数量不正确时应返回false");
    }
    
    @Test
    public void testValidateResult_WithEmptyMarkdown() {
        // 准备测试数据
        String markdownResult = "";
        
        // 执行验证
        boolean result = validator.validateResult(markdownResult, 2);
        
        // 断言结果
        assertFalse(result, "Markdown为空时应返回false");
    }
    
    @Test
    public void testValidateResult_WithNullMarkdown() {
        // 执行验证
        boolean result = validator.validateResult(null, 2);
        
        // 断言结果
        assertFalse(result, "Markdown为null时应返回false");
    }
    
    @Test
    public void testValidateResult_WithNoChapters() {
        // 准备测试数据
        String markdownResult = """
                这是一段没有章节标题的Markdown文本。
                它不包含任何章节标记，因此章节数量应该为0。
                """;
        
        // 执行验证
        boolean result = validator.validateResult(markdownResult, 0);
        
        // 断言结果
        assertTrue(result, "没有章节时，预期章节数为0应返回true");
    }
}
