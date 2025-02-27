package com.novelreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 章节分割器，将小说内容按章节分割，并合并章节生成分割文件
 */
public class ChapterSplitter {
    private static final Logger logger = LoggerFactory.getLogger(ChapterSplitter.class);
    private static final int CHAPTERS_PER_GROUP = 10;
    
    /**
     * 将小说按章节分割成多个组，每组10章
     * @param chapters 章节列表
     * @param outputDirectory 输出目录
     * @return 章节组列表
     */
    public List<ChapterGroup> splitNovelIntoChapterGroups(List<Chapter> chapters, String outputDirectory) {
        List<ChapterGroup> chapterGroups = new ArrayList<>();
        
        // 创建输出目录
        File outputDir = new File(outputDirectory);
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            logger.error("Failed to create output directory: {}", outputDirectory);
            throw new RuntimeException("Failed to create output directory: " + outputDirectory);
        }
        
        int totalChapters = chapters.size();
        int groupCount = (int) Math.ceil((double) totalChapters / CHAPTERS_PER_GROUP);
        
        for (int i = 0; i < groupCount; i++) {
            int startChapterIndex = i * CHAPTERS_PER_GROUP;
            int endChapterIndex = Math.min((i + 1) * CHAPTERS_PER_GROUP - 1, totalChapters - 1);
            
            int startChapterNumber = chapters.get(startChapterIndex).getChapterNumber();
            int endChapterNumber = chapters.get(endChapterIndex).getChapterNumber();
            
            ChapterGroup group = new ChapterGroup(i + 1, startChapterNumber, endChapterNumber);
            
            for (int j = startChapterIndex; j <= endChapterIndex; j++) {
                group.addChapter(chapters.get(j));
            }
            
            // 写入文件
            writeChapterGroupToFile(group, outputDirectory);
            
            chapterGroups.add(group);
            logger.info("Created chapter group {}: {} chapters ({})", 
                    group.getChapterGroupId(), group.getChapterCount(), group.getFileName());
        }
        
        return chapterGroups;
    }
    
    /**
     * 将章节组写入文件
     * @param group 章节组
     * @param outputDirectory 输出目录
     */
    public void writeChapterGroupToFile(ChapterGroup group, String outputDirectory) {
        String filePath = outputDirectory + File.separator + group.getFileName();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(group.getContent());
            logger.info("Written chapter group to file: {}", filePath);
        } catch (IOException e) {
            logger.error("Error writing chapter group to file: {}", filePath, e);
            throw new RuntimeException("Error writing chapter group to file", e);
        }
    }
}
