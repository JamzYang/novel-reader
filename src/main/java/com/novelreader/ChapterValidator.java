package com.novelreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 章节校验器，验证章节顺序，检测重复/缺失章节
 */
public class ChapterValidator {
    private static final Logger logger = LoggerFactory.getLogger(ChapterValidator.class);
    
    /**
     * 验证章节顺序和完整性
     * @param chapters 章节列表
     * @return 验证结果
     */
    public ValidationResult validateChapterOrder(List<Chapter> chapters) {
        if (chapters == null || chapters.isEmpty()) {
            return ValidationResult.failure("No chapters found");
        }
        
        // 检查章节顺序和重复/缺失
        Set<Integer> chapterNumbers = new HashSet<>();
        int expectedChapterNumber = 1;
        
        for (Chapter chapter : chapters) {
            int chapterNumber = chapter.getChapterNumber();
            
            // 检查重复章节
            if (chapterNumbers.contains(chapterNumber)) {
                String errorMessage = "Duplicate chapter found: " + chapter.getChapterTitle();
                logger.error(errorMessage);
                return ValidationResult.failure(errorMessage);
            }
            
            // 检查章节顺序
            if (chapterNumber != expectedChapterNumber) {
                String errorMessage = "Chapter sequence error: expected chapter " + expectedChapterNumber + 
                        ", but found " + chapter.getChapterTitle();
                logger.error(errorMessage);
                return ValidationResult.failure(errorMessage);
            }
            
            chapterNumbers.add(chapterNumber);
            expectedChapterNumber++;
        }
        
        logger.info("Chapter validation successful. Total chapters: {}", chapters.size());
        return ValidationResult.success();
    }
}
