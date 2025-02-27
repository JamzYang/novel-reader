package com.novelreader;

/**
 * 表示小说中的一个章节
 */
public class Chapter {
    private int chapterNumber;
    private String chapterTitle;
    private String chapterContent;
    
    public Chapter(int chapterNumber, String chapterTitle, String chapterContent) {
        this.chapterNumber = chapterNumber;
        this.chapterTitle = chapterTitle;
        this.chapterContent = chapterContent;
    }
    
    // Getters and Setters
    public int getChapterNumber() {
        return chapterNumber;
    }
    
    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }
    
    public String getChapterTitle() {
        return chapterTitle;
    }
    
    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }
    
    public String getChapterContent() {
        return chapterContent;
    }
    
    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }
    
    @Override
    public String toString() {
        return chapterTitle;
    }
}
