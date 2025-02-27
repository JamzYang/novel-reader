package com.novelreader;

/**
 * API请求对象，封装API请求参数
 */
public class ApiRequest {
    private String modelName;
    private String prompt;
    private String chapterGroupContent;
    
    public ApiRequest(String modelName, String prompt, String chapterGroupContent) {
        this.modelName = modelName;
        this.prompt = prompt;
        this.chapterGroupContent = chapterGroupContent;
    }
    
    // Getters and Setters
    public String getModelName() {
        return modelName;
    }
    
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public String getChapterGroupContent() {
        return chapterGroupContent;
    }
    
    public void setChapterGroupContent(String chapterGroupContent) {
        this.chapterGroupContent = chapterGroupContent;
    }
}
