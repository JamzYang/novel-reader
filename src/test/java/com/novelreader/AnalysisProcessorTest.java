package com.novelreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AnalysisProcessor的单元测试
 */
@ExtendWith(MockitoExtension.class)
public class AnalysisProcessorTest {
    
    @Mock
    private GeminiApiClient mockApiClient;
    
    @Mock
    private ResultValidator mockValidator;
    
    @Mock
    private ResultSaver mockResultSaver;
    
    @TempDir
    Path tempDir;
    
    private AnalysisProcessor processor;
    private String outputDirectory;
    private String finalOutputFile;
    
    @BeforeEach
    public void setUp() {
        outputDirectory = tempDir.toString();
        finalOutputFile = Paths.get(outputDirectory, "final_results.json").toString();
        
        processor = new AnalysisProcessor(
                mockApiClient,
                mockValidator,
                mockResultSaver,
                outputDirectory,
                finalOutputFile
        );
    }
    
    @Test
    public void testProcessChapterGroups_Success() {
        // 准备测试数据
        ChapterGroup group1 = new ChapterGroup(1, 1, 2);
        group1.addChapter(new Chapter(1, "第1章", "测试内容1-1"));
        group1.addChapter(new Chapter(2, "第2章", "测试内容1-2"));
        
        ChapterGroup group2 = new ChapterGroup(2, 3, 4);
        group2.addChapter(new Chapter(3, "第3章", "测试内容2-1"));
        group2.addChapter(new Chapter(4, "第4章", "测试内容2-2"));
        
        List<ChapterGroup> chapterGroups = Arrays.asList(group1, group2);
        
        String resultFile1 = Paths.get(outputDirectory, "result1.json").toString();
        String resultFile2 = Paths.get(outputDirectory, "result2.json").toString();
        
        // 配置模拟对象行为
        ApiResponse successResponse1 = ApiResponse.success("成功的Markdown结果1");
        ApiResponse successResponse2 = ApiResponse.success("成功的Markdown结果2");
        
        when(mockApiClient.analyzeChapterGroup(any())).thenReturn(successResponse1, successResponse2);
        when(mockValidator.validateResult(anyString(), anyInt())).thenReturn(true);
        when(mockResultSaver.saveChapterGroupResult(eq(group1), anyString(), eq(outputDirectory))).thenReturn(resultFile1);
        when(mockResultSaver.saveChapterGroupResult(eq(group2), anyString(), eq(outputDirectory))).thenReturn(resultFile2);
        when(mockResultSaver.mergeResults(Arrays.asList(resultFile1, resultFile2), finalOutputFile)).thenReturn(true);
        
        // 执行处理
        boolean result = processor.processChapterGroups(chapterGroups);
        
        // 验证结果
        assertTrue(result, "处理应该成功");
        
        // 验证模拟对象的调用
        verify(mockApiClient, times(2)).analyzeChapterGroup(any());
        verify(mockValidator, times(2)).validateResult(anyString(), anyInt());
        verify(mockResultSaver, times(1)).saveChapterGroupResult(eq(group1), anyString(), eq(outputDirectory));
        verify(mockResultSaver, times(1)).saveChapterGroupResult(eq(group2), anyString(), eq(outputDirectory));
        verify(mockResultSaver, times(1)).mergeResults(Arrays.asList(resultFile1, resultFile2), finalOutputFile);
    }
    
    @Test
    public void testProcessChapterGroups_ApiFailureWithRetry() {
        // 准备测试数据
        ChapterGroup group = new ChapterGroup(1, 1, 2);
        group.addChapter(new Chapter(1, "第1章", "测试内容1"));
        group.addChapter(new Chapter(2, "第2章", "测试内容2"));
        List<ChapterGroup> chapterGroups = List.of(group);
        
        String resultFile = Paths.get(outputDirectory, "result.json").toString();
        
        // 配置模拟对象行为 - 第一次API调用失败，第二次成功
        ApiResponse failureResponse = ApiResponse.failure("API调用失败");
        ApiResponse successResponse = ApiResponse.success("成功的Markdown结果");
        
        when(mockApiClient.analyzeChapterGroup(any())).thenReturn(failureResponse, successResponse);
        when(mockValidator.validateResult(anyString(), anyInt())).thenReturn(true);
        when(mockResultSaver.saveChapterGroupResult(eq(group), anyString(), eq(outputDirectory))).thenReturn(resultFile);
        when(mockResultSaver.mergeResults(List.of(resultFile), finalOutputFile)).thenReturn(true);
        
        // 执行处理
        boolean result = processor.processChapterGroups(chapterGroups);
        
        // 验证结果
        assertTrue(result, "处理应该成功");
        
        // 验证模拟对象的调用
        verify(mockApiClient, times(2)).analyzeChapterGroup(any());
        verify(mockValidator, times(1)).validateResult(anyString(), anyInt());
        verify(mockResultSaver, times(1)).saveChapterGroupResult(eq(group), anyString(), eq(outputDirectory));
        verify(mockResultSaver, times(1)).mergeResults(List.of(resultFile), finalOutputFile);
    }
    
    @Test
    public void testProcessChapterGroups_ValidationFailureWithRetry() {
        // 准备测试数据
        ChapterGroup group = new ChapterGroup(1, 1, 2);
        group.addChapter(new Chapter(1, "第1章", "测试内容1"));
        group.addChapter(new Chapter(2, "第2章", "测试内容2"));
        List<ChapterGroup> chapterGroups = List.of(group);
        
        String resultFile = Paths.get(outputDirectory, "result.json").toString();
        
        // 配置模拟对象行为 - 第一次验证失败，第二次成功
        ApiResponse successResponse = ApiResponse.success("成功的Markdown结果");
        
        when(mockApiClient.analyzeChapterGroup(any())).thenReturn(successResponse);
        when(mockValidator.validateResult(anyString(), anyInt())).thenReturn(false, true);
        when(mockResultSaver.saveChapterGroupResult(eq(group), anyString(), eq(outputDirectory))).thenReturn(resultFile);
        when(mockResultSaver.mergeResults(List.of(resultFile), finalOutputFile)).thenReturn(true);
        
        // 执行处理
        boolean result = processor.processChapterGroups(chapterGroups);
        
        // 验证结果
        assertTrue(result, "处理应该成功");
        
        // 验证模拟对象的调用
        verify(mockApiClient, times(2)).analyzeChapterGroup(any());
        verify(mockValidator, times(2)).validateResult(anyString(), anyInt());
        verify(mockResultSaver, times(1)).saveChapterGroupResult(eq(group), anyString(), eq(outputDirectory));
        verify(mockResultSaver, times(1)).mergeResults(List.of(resultFile), finalOutputFile);
    }
    
    @Test
    public void testProcessChapterGroups_MaxRetryExceeded() {
        // 准备测试数据
        ChapterGroup group = new ChapterGroup(1, 1, 2);
        group.addChapter(new Chapter(1, "第1章", "测试内容1"));
        group.addChapter(new Chapter(2, "第2章", "测试内容2"));
        List<ChapterGroup> chapterGroups = List.of(group);
        
        // 配置模拟对象行为 - 所有验证都失败
        ApiResponse successResponse = ApiResponse.success("成功的Markdown结果");
        
        when(mockApiClient.analyzeChapterGroup(any())).thenReturn(successResponse);
        when(mockValidator.validateResult(anyString(), anyInt())).thenReturn(false);
        
        // 执行处理
        boolean result = processor.processChapterGroups(chapterGroups);
        
        // 验证结果
        assertFalse(result, "处理应该失败");
        
        // 验证模拟对象的调用 - 应该尝试4次（初始+3次重试）
        verify(mockApiClient, times(4)).analyzeChapterGroup(any());
        verify(mockValidator, times(4)).validateResult(anyString(), anyInt());
        verify(mockResultSaver, never()).saveChapterGroupResult(any(), anyString(), anyString());
        verify(mockResultSaver, never()).mergeResults(anyList(), anyString());
    }
    
    @Test
    public void testProcessChapterGroups_SaveResultFailure() {
        // 准备测试数据
        ChapterGroup group = new ChapterGroup(1, 1, 2);
        group.addChapter(new Chapter(1, "第1章", "测试内容1"));
        group.addChapter(new Chapter(2, "第2章", "测试内容2"));
        List<ChapterGroup> chapterGroups = List.of(group);
        
        // 配置模拟对象行为 - 保存结果失败
        ApiResponse successResponse = ApiResponse.success("成功的Markdown结果");
        
        when(mockApiClient.analyzeChapterGroup(any())).thenReturn(successResponse);
        when(mockValidator.validateResult(anyString(), anyInt())).thenReturn(true);
        when(mockResultSaver.saveChapterGroupResult(eq(group), anyString(), eq(outputDirectory))).thenReturn(null);
        
        // 执行处理
        boolean result = processor.processChapterGroups(chapterGroups);
        
        // 验证结果
        assertFalse(result, "处理应该失败");
        
        // 验证模拟对象的调用
        verify(mockApiClient, times(1)).analyzeChapterGroup(any());
        verify(mockValidator, times(1)).validateResult(anyString(), anyInt());
        verify(mockResultSaver, times(1)).saveChapterGroupResult(eq(group), anyString(), eq(outputDirectory));
        verify(mockResultSaver, never()).mergeResults(anyList(), anyString());
    }
}
