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
 * OpenAI API客户端，封装API调用细节
 */
public class OpenAiApiClient implements ApiClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAiApiClient.class);
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final int TIMEOUT_SECONDS = 240;
    
    private final String apiKey;
    private final RateLimiter rateLimiter;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public OpenAiApiClient(String apiKey, RateLimiter rateLimiter) {
        this.apiKey = apiKey;
        this.rateLimiter = rateLimiter;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
        this.objectMapper = new ObjectMapper();
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
            
            // 构建OpenAI请求体，格式与Gemini不同
            Map<String, Object> requestBody = new HashMap<>();
            // 设置模型，将Gemini模型名称映射到OpenAI模型
            requestBody.put("model", mapModelName("GPT-4o"));
            
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
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                logger.info("OpenAI API call successful for chapter group");
                
                // 解析OpenAI响应并转换为通用格式
                String extractedContent = extractContentFromOpenAiResponse(responseBody);
                return ApiResponse.success(extractedContent);
            } else {
                String errorMessage = "OpenAI API call failed with status code: " + response.statusCode() + ", body: " + response.body();
                logger.error(errorMessage);
                return ApiResponse.failure(errorMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("OpenAI API call interrupted", e);
            return ApiResponse.failure("OpenAI API call interrupted: " + e.getMessage());
        } catch (IOException e) {
            logger.error("OpenAI API call failed", e);
            return ApiResponse.failure("OpenAI API call failed: " + e.getMessage());
        } finally {
            rateLimiter.release();
        }
    }

    @Override
    public String getProviderName() {
        return "openai";
    }

    /**
     * 将Gemini模型名称映射到OpenAI模型名称
     * @param geminiModelName Gemini模型名称
     * @return 对应的OpenAI模型名称
     */
    private String mapModelName(String geminiModelName) {
        // 根据Gemini模型名称选择适当的OpenAI模型
        switch (geminiModelName) {
            case "gemini-2.0-flash":
                return "gpt-4-turbo";
            case "gemini-1.5-pro":
                return "gpt-4";
            default:
                return "gpt-3.5-turbo"; // 默认模型
        }
    }
    
    /**
     * 从OpenAI响应中提取内容
     * @param responseBody OpenAI响应体
     * @return 提取的内容
     */
    private String extractContentFromOpenAiResponse(String responseBody) {
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
            
            logger.error("Failed to extract content from OpenAI response: " + responseBody);
            return "";
        } catch (Exception e) {
            logger.error("Error parsing OpenAI response", e);
            return "";
        }
    }
}
