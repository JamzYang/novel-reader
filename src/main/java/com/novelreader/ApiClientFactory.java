package com.novelreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API客户端工厂类，负责创建不同类型的API客户端
 */
public class ApiClientFactory {
    private static final Logger logger = LoggerFactory.getLogger(ApiClientFactory.class);
    
    /**
     * 创建API客户端
     * @param providerName 提供商名称
     * @param rateLimiter 速率限制器
     * @return 对应类型的API客户端
     */
    public static ApiClient createApiClient(String providerName, RateLimiter rateLimiter) {
        logger.info("创建API客户端: {}", providerName);
        
        switch (providerName) {
            case "gemini":
                return new GeminiApiClient(rateLimiter);
            case "deepseek":
                return new DeepSeekApiClient(rateLimiter);
            default:
                throw new IllegalArgumentException("不支持的API提供商: " + providerName);
        }
    }
    
    /**
     * 使用当前配置的提供商创建API客户端
     * @param rateLimiter 速率限制器
     * @return API客户端
     */
    public static ApiClient createApiClient(RateLimiter rateLimiter) {
        return createApiClient(Configuration.getCurrentProvider(), rateLimiter);
    }
}
