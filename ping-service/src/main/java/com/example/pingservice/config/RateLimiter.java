package com.example.pingservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class RateLimiter {

    private final FileChannel fileChannel;
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private FileLock lock;
    private long lastResetTime = System.currentTimeMillis();

    public RateLimiter() {
        try {
            File lockFile = new File("rate_limit.lock");
            RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
            fileChannel = randomAccessFile.getChannel();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize rate limiter", e);
        }
    }

    public synchronized boolean tryAcquire() {
        //synchronized 保障单线程并发安全
        try {
            // 尝试获取文件锁
            if (lock == null || !lock.isValid()) {
                lock = fileChannel.lock();
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastResetTime >= 1000) {
                // 新的时间窗口，重置计数器
                requestCount.set(0);
                lastResetTime = currentTime;
            }

            if (requestCount.get() < 2) {
                // 增加计数器，每秒最多2次请求
                requestCount.incrementAndGet();
//                log.info("Request sent");
                return true;
            }
        } catch (IOException e) {
            log.error("Error occurred while trying to acquire rate limit: {}", e.getMessage());
        } finally {
            // 释放锁，释放资源
            if (lock != null && lock.isValid()) {
                try {
                    lock.release();
                } catch (IOException e) {
                    log.error("Error occurred while releasing lock: {}", e.getMessage());
                }
            }
        }
        return false;
    }

}
