package com.novelreader;

/**
 * API客户端接口，定义统一的API调用方法
 */
public interface ApiClient {
    /**
     * 分析章节组内容
     * @param request API请求
     * @return API响应
     */
    ApiResponse analyzeChapterGroup(ApiRequest request);
    
    /**
     * 获取API提供商名称
     * @return API提供商名称
     */
    String getProviderName();
}
