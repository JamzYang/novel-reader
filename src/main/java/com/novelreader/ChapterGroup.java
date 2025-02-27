package com.novelreader;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示一组章节，用于API调用
 */
public class ChapterGroup {
    private int chapterGroupId;
    private int startChapterNumber;
    private int endChapterNumber;
    private List<Chapter> chapters;
    
    public ChapterGroup(int chapterGroupId, int startChapterNumber, int endChapterNumber) {
        this.chapterGroupId = chapterGroupId;
        this.startChapterNumber = startChapterNumber;
        this.endChapterNumber = endChapterNumber;
        this.chapters = new ArrayList<>();
    }
    
    public void addChapter(Chapter chapter) {
        chapters.add(chapter);
    }
    
    public String getFileName() {
        return String.format("%03d第%d-%d章.txt", chapterGroupId, startChapterNumber, endChapterNumber);
    }
    
    public String getAnalysisFileName() {
        return String.format("%03d第%d-%d章_分析.json", chapterGroupId, startChapterNumber, endChapterNumber);
    }
    
    public String getContent() {
        StringBuilder content = new StringBuilder();
        for (Chapter chapter : chapters) {
            content.append(chapter.getChapterTitle()).append("\n\n");
            content.append(chapter.getChapterContent()).append("\n\n");
        }
        return content.toString();
    }
    
    // Getters and Setters
    public int getChapterGroupId() {
        return chapterGroupId;
    }
    
    public void setChapterGroupId(int chapterGroupId) {
        this.chapterGroupId = chapterGroupId;
    }
    
    public int getStartChapterNumber() {
        return startChapterNumber;
    }
    
    public void setStartChapterNumber(int startChapterNumber) {
        this.startChapterNumber = startChapterNumber;
    }
    
    public int getEndChapterNumber() {
        return endChapterNumber;
    }
    
    public void setEndChapterNumber(int endChapterNumber) {
        this.endChapterNumber = endChapterNumber;
    }
    
    public List<Chapter> getChapters() {
        return chapters;
    }
    
    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
    
    public int getChapterCount() {
        return chapters.size();
    }
}
