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
            // initialize file lock
            File lockFile = new File("rate_limit.lock");
            RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
            fileChannel = randomAccessFile.getChannel();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize rate limiter", e);
        }
    }

    public synchronized boolean tryAcquire() {
        //synchronized ensure single thread security
        try {
            // try to get file lock
            if (lock == null || !lock.isValid()) {
                lock = fileChannel.lock();
            }
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastResetTime >= 1000) {
                // new time, reset counter
                requestCount.set(0);
                lastResetTime = currentTime;
            }

            if (requestCount.get() < 2) {
                // count + 1，2 times per second
                requestCount.incrementAndGet();
//                log.info("Request sent");
                return true;
            }
        } catch (IOException e) {
            log.error("Error occurred while trying to acquire rate limit: {}", e.getMessage());
        } finally {
            // release lock & resource
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
