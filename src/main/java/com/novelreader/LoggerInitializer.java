package com.novelreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志初始化器，配置Logback日志系统
 */
public class LoggerInitializer {
    private static final Logger logger = LoggerFactory.getLogger(LoggerInitializer.class);
    
    /**
     * 初始化日志系统
     */
    public void initializeLogger() {
        // Logback会自动从classpath中加载logback.xml
        logger.info("Logger initialized successfully");
    }
}
