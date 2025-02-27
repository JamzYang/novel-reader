package com.novelreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 章节识别器，使用正则表达式识别章节标题
 */
public class ChapterIdentifier {
    private static final Logger logger = LoggerFactory.getLogger(ChapterIdentifier.class);
    private static final Pattern CHAPTER_PATTERN = Pattern.compile("第(\\d+)章");
    
    /**
     * 识别小说文件中的所有章节
     * @param filePath 小说文件路径
     * @return 识别出的章节列表
     */
    public List<Chapter> identifyChapters(String filePath) {
        List<Chapter> chapters = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            String currentTitle = null;
            int currentChapterNumber = -1;
            
            while ((line = reader.readLine()) != null) {
                Matcher matcher = CHAPTER_PATTERN.matcher(line);
                if (matcher.find()) {
                    // 如果已经有章节内容，保存前一章节
                    if (currentTitle != null) {
                        chapters.add(new Chapter(currentChapterNumber, currentTitle, content.toString().trim()));
                        content = new StringBuilder();
                    }
                    
                    // 提取章节号
                    currentChapterNumber = Integer.parseInt(matcher.group(1));
                    currentTitle = line.trim();
                    logger.info("Identified chapter: {}", currentTitle);
                } else if (currentTitle != null) {
                    // 累积章节内容
                    content.append(line).append("\n");
                }
            }
            
            // 保存最后一章
            if (currentTitle != null) {
                chapters.add(new Chapter(currentChapterNumber, currentTitle, content.toString().trim()));
            }
            
            logger.info("Total chapters identified: {}", chapters.size());
            return chapters;
        } catch (IOException e) {
            logger.error("Error reading novel file: {}", filePath, e);
            throw new RuntimeException("Error reading novel file", e);
        }
    }
}
