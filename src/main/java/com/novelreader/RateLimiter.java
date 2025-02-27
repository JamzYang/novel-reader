package com.novelreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 限流器，使用信号量实现每分钟15次的API调用限制
 */
public class RateLimiter {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);
    
    private final Semaphore semaphore;
    private final int permitsPerMinute;
    
    public RateLimiter(int permitsPerMinute) {
        this.permitsPerMinute = permitsPerMinute;
        this.semaphore = new Semaphore(permitsPerMinute);
        
        // 每分钟重置信号量
        Thread replenishThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.MINUTES.sleep(1);
                    int permits = permitsPerMinute - semaphore.availablePermits();
                    if (permits > 0) {
                        semaphore.release(permits);
                        logger.info("Replenished {} permits", permits);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Rate limiter replenish thread interrupted");
                    break;
                }
            }
        });
        replenishThread.setDaemon(true);
        replenishThread.start();
    }
    
    /**
     * 获取一个许可，如果没有可用许可则阻塞
     * @throws InterruptedException 如果线程被中断
     */
    public void acquire() throws InterruptedException {
        semaphore.acquire();
        logger.debug("Acquired permit, remaining: {}", semaphore.availablePermits());
    }
    
    /**
     * 释放一个许可
     */
    public void release() {
        semaphore.release();
        logger.debug("Released permit, remaining: {}", semaphore.availablePermits());
    }
}
