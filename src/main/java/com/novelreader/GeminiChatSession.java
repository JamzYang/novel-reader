package com.novelreader;

import com.google.cloud.aiplatform.v1beta1.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeminiChatSession {

  private static final Logger logger = LoggerFactory.getLogger(GeminiChatSession.class);
  private static final String PROJECT_ID = "your-project-id"; // 替换成你的 GCP 项目 ID
  private static final String LOCATION = "us-central1"; // 替换成你的 GCP 区域
  private static final String MODEL_NAME = "gemini-pro"; // 模型名称
  private static final String ENDPOINT = LOCATION + "-aiplatform.googleapis.com:443";

  private final List<Content> conversationHistory = new ArrayList<>();

  public static void main(String[] args) throws IOException {
    GeminiChatSession chatSession = new GeminiChatSession();
    Scanner scanner = new Scanner(System.in);

    System.out.println("开始对话 (输入 'exit' 退出)");
    while (true) {
      System.out.print("你: ");
      String userInput = scanner.nextLine();
      if (userInput.equalsIgnoreCase("exit")) {
        break;
      }

      String geminiResponse = chatSession.sendMessage(userInput);
      System.out.println("Gemini: " + geminiResponse);
    }

    scanner.close();
  }

  public String sendMessage(String userInput) throws IOException {
    // 1. 构建用户输入 Content
    Content userContent = Content.newBuilder().setRole("user").addParts(Part.newBuilder().setText(userInput).build()).build();
    conversationHistory.add(userContent);

    // 2. 构建 GenerateContentRequest
    GenerateContentRequest request = buildGenerateContentRequest(conversationHistory);

    // 3. 发送请求到 Gemini API
    GenerateContentResponse response = predict(PROJECT_ID, LOCATION, MODEL_NAME, request);

    // 4. 解析响应，提取 Gemini 的回复
    String geminiResponse = parseResponse(response);

    // 5. 将 Gemini 的回复添加到对话历史
    Content modelContent = Content.newBuilder().setRole("model").addParts(Part.newBuilder().setText(geminiResponse).build()).build();
    conversationHistory.add(modelContent);
    return geminiResponse;
  }

  private GenerateContentRequest buildGenerateContentRequest(List<Content> history) {
    // 可以配置生成参数，例如 maxOutputTokens
    GenerationConfig generationConfig = GenerationConfig.newBuilder().setMaxOutputTokens(200).build();

    return GenerateContentRequest.newBuilder()
        .setModel(ModelName.of(PROJECT_ID, LOCATION, MODEL_NAME).toString())
        .addAllContents(history)
        .setGenerationConfig(generationConfig)
        .build();
  }

  public GenerateContentResponse predict(String project, String location, String model, GenerateContentRequest request) throws IOException {
    PredictionServiceSettings predictServiceSettings =
        PredictionServiceSettings.newBuilder().setEndpoint(ENDPOINT).build();

    try (PredictionServiceClient client = PredictionServiceClient.create(predictServiceSettings)) {
      ModelName modelName = ModelName.of(project, location, model);
      // 使用更方便的 generateContent 方法
      return client.generateContent(request);
    }
  }

  private String parseResponse(GenerateContentResponse response) {
    if (response != null && response.getCandidatesCount() > 0) {  // 使用 getCandidatesCount()
      Candidate candidate = response.getCandidates(0);
      if (candidate.getContent().getPartsCount() > 0) {
        return candidate.getContent().getParts(0).getText();
      }
    }
    return "No response text found.";
  }
}
