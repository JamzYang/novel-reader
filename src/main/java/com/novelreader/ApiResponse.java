package com.novelreader;

/**
 * API响应对象，封装API响应结果
 */
public record ApiResponse (
     boolean success,
     String responseBody,
     String errorMessage
) {
  public boolean isSuccess() {
    return success;
  }

  public static ApiResponse success(String responseBody) {
    return new ApiResponse(true, responseBody, null);
  }

  public static ApiResponse failure(String errorMessage) {
    return new ApiResponse(false, null, errorMessage);
  }

}
