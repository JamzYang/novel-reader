package com.novelreader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 限流器的单元测试
 */
public class RateLimiterTest {
    
    private RateLimiter rateLimiter;
    private final int permitsPerMinute = 5;
    
    @BeforeEach
    public void setUp() {
        rateLimiter = new RateLimiter(permitsPerMinute);
    }
    
    @Test
    public void testAcquireAndRelease() throws InterruptedException {
        // 测试获取和释放许可
        for (int i = 0; i < permitsPerMinute; i++) {
            rateLimiter.acquire();
        }
        
        // 释放一个许可
        rateLimiter.release();
        
        // 现在应该可以再获取一个许可
        rateLimiter.acquire();
        
        // 这里不应该抛出异常
        assertTrue(true, "测试通过");
    }
    
    @Test
    public void testConcurrentAcquire() throws InterruptedException {
        // 测试并发获取许可
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // 等待所有线程准备就绪
                    
                    // 尝试获取许可
                    try {
                        rateLimiter.acquire();
                        successCount.incrementAndGet();
                        Thread.sleep(10); // 模拟一些处理时间
                        rateLimiter.release();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }
        
        startLatch.countDown(); // 所有线程开始执行
        finishLatch.await(5, TimeUnit.SECONDS); // 等待所有线程完成
        
        // 由于我们有permitsPerMinute个许可，所以至少应该有permitsPerMinute个线程成功获取许可
        assertTrue(successCount.get() >= permitsPerMinute, 
                "至少应有" + permitsPerMinute + "个线程成功获取许可，实际成功数: " + successCount.get());
        
        executor.shutdownNow();
    }
    
    @Test
    public void testRateLimiterReplenish() throws InterruptedException {
        // 注意：这个测试需要等待一分钟，可能会导致测试运行时间较长
        // 在实际测试中，你可能需要模拟时间或使用更短的重置周期
        
        // 首先获取所有许可
        for (int i = 0; i < permitsPerMinute; i++) {
            rateLimiter.acquire();
        }
        
        // 尝试获取另一个许可，这应该会阻塞
        // 为了避免测试阻塞，我们使用一个单独的线程
        AtomicInteger acquiredAfterReplenish = new AtomicInteger(0);
        Thread testThread = new Thread(() -> {
            try {
                rateLimiter.acquire();
                acquiredAfterReplenish.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        testThread.start();
        
        // 等待一小段时间，确保线程已经开始尝试获取许可
        Thread.sleep(100);
        
        // 此时应该没有成功获取许可
        assertEquals(0, acquiredAfterReplenish.get(), "在重置前不应该获取到许可");
        
        // 手动释放一个许可，模拟重置
        rateLimiter.release();
        
        // 等待线程完成
        testThread.join(1000);
        
        // 现在应该已经获取到许可
        assertEquals(1, acquiredAfterReplenish.get(), "在释放许可后应该获取到许可");
    }
}
