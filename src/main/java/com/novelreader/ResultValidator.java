package com.novelreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 结果验证器，用于验证Gemini API返回的Markdown结果
 */
public class ResultValidator {
    private static final Logger logger = LoggerFactory.getLogger(ResultValidator.class);
    
    // 用于匹配Markdown中的章节标题的正则表达式
    private static final Pattern CHAPTER_PATTERN = Pattern.compile("\\*\\*第(\\d+)章");
    
    /**
     * 验证API返回的Markdown结果中包含的章节数量是否与预期一致
     * 
     * @param markdownResult Gemini API返回的Markdown结果
     * @param expectedChapterCount 预期的章节数量
     * @return 如果章节数量一致返回true，否则返回false
     */
    public boolean validateResult(String markdownResult, int expectedChapterCount) {
        if (markdownResult == null || markdownResult.isEmpty()) {
            logger.error("Markdown结果为空");
            return false;
        }
        
        int actualChapterCount = countChaptersInMarkdown(markdownResult);
        logger.info("预期章节数量: {}, 实际章节数量: {}", expectedChapterCount, actualChapterCount);
        
        return actualChapterCount == expectedChapterCount;
    }
    
    /**
     * 统计Markdown结果中包含的章节数量
     * 
     * @param markdownResult Markdown结果
     * @return 章节数量
     */
    private int countChaptersInMarkdown(String markdownResult) {
        Matcher matcher = CHAPTER_PATTERN.matcher(markdownResult);
        int count = 0;
        
        while (matcher.find()) {
            count++;
        }
        
        return count;
    }
}
