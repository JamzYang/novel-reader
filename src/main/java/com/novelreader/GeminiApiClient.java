package com.novelreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Gemini API客户端，封装API调用细节
 */
public class GeminiApiClient implements ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(GeminiApiClient.class);
    private static final int TIMEOUT_SECONDS = 240;
    private static final String PROVIDER_NAME = "gemini";
    
    private final RateLimiter rateLimiter;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public GeminiApiClient(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
    
    /**
     * 分析章节组内容
     * @param request API请求
     * @return API响应
     */
    @Override
    public ApiResponse analyzeChapterGroup(ApiRequest request) {
        try {
            rateLimiter.acquire();
            
            // 从配置中获取URL和API密钥
            String apiKey = Configuration.getProviderConfig(PROVIDER_NAME, "api_key");
            String modelName = Configuration.getProviderConfig(PROVIDER_NAME, "model");
            String urlTemplate = Configuration.getProviderConfig(PROVIDER_NAME, "url");
            
            // 替换URL模板中的占位符
            String url = urlTemplate
                    .replace("{model}", URLEncoder.encode(modelName, StandardCharsets.UTF_8))
                    .replace("{api_key}", URLEncoder.encode(apiKey, StandardCharsets.UTF_8));
            
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contents = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            
            parts.put("text", request.getPrompt() + "\n\n" + request.getChapterGroupContent());
            contents.put("parts", new Object[]{parts});
            requestBody.put("contents", new Object[]{contents});
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                logger.info("API call successful for chapter group");
                return ApiResponse.success(responseBody);
            } else {
                String errorMessage = "API call failed with status code: " + response.statusCode() + ", body: " + response.body();
                logger.error(errorMessage);
                return ApiResponse.failure(errorMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("API call interrupted", e);
            return ApiResponse.failure("API call interrupted: " + e.getMessage());
        } catch (IOException e) {
            logger.error("API call failed", e);
            return ApiResponse.failure("API call failed: " + e.getMessage());
        } finally {
            rateLimiter.release();
        }
    }
}
