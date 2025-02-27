package com.novelreader;

/**
 * 表示章节校验的结果
 */
public class ValidationResult {
    private boolean valid;
    private String errorMessage;
    
    public ValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }
    
    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }
    
    public static ValidationResult failure(String errorMessage) {
        return new ValidationResult(false, errorMessage);
    }
    
    // Getters
    public boolean isValid() {
        return valid;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
}
