package com.novelreader;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DeepSeek API客户端，封装API调用细节
 */
public class DeepSeekApiClient implements ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekApiClient.class);
    private static final int TIMEOUT_SECONDS = 240;
    private static final String PROVIDER_NAME = "deepseek";
    
    private final RateLimiter rateLimiter;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public DeepSeekApiClient(RateLimiter rateLimiter) {
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
            String modelName = Configuration.getProviderConfig(PROVIDER_NAME, "deepseek"); // 在配置中是"deepseek"
            String url = Configuration.getProviderConfig(PROVIDER_NAME, "url");
            
            // 构建DeepSeek请求格式
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("stream", false);
            
            // 构建消息
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 系统消息（提示）
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", request.getPrompt());
            messages.add(systemMessage);
            
            // 用户消息（章节内容）
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", request.getChapterGroupContent());
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                logger.info("DeepSeek API call successful for chapter group");
                
                // 解析DeepSeek响应并提取内容
                String extractedContent = extractContentFromDeepSeekResponse(responseBody);
                return ApiResponse.success(extractedContent);
            } else {
                String errorMessage = "DeepSeek API call failed with status code: " + response.statusCode() + ", body: " + response.body();
                logger.error(errorMessage);
                return ApiResponse.failure(errorMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("DeepSeek API call interrupted", e);
            return ApiResponse.failure("DeepSeek API call interrupted: " + e.getMessage());
        } catch (IOException e) {
            logger.error("DeepSeek API call failed", e);
            return ApiResponse.failure("DeepSeek API call failed: " + e.getMessage());
        } finally {
            rateLimiter.release();
        }
    }
    
    /**
     * 从DeepSeek响应中提取内容
     * @param responseBody DeepSeek响应体
     * @return 提取的内容
     */
    private String extractContentFromDeepSeekResponse(String responseBody) {
        try {
            // 解析JSON响应
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            
            // 获取choices部分
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            if (choices != null && !choices.isEmpty()) {
                // 获取第一个选择的消息
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null) {
                    // 返回消息内容
                    return (String) message.get("content");
                }
            }
            
            logger.error("Failed to extract content from DeepSeek response: " + responseBody);
            return "";
        } catch (Exception e) {
            logger.error("Error parsing DeepSeek response", e);
            return "";
        }
    }
}
