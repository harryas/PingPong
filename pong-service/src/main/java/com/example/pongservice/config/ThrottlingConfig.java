package com.example.pongservice.config;

import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class ThrottlingConfig {
    //计数器
    private final AtomicInteger requestCount = new AtomicInteger(0);

    public boolean canHandleRequest() {
        int currentCount = requestCount.getAndIncrement();
        if (currentCount == 0) {
            // 每秒重置计数器
            Schedulers.single().schedule(() -> requestCount.set(0), 1, TimeUnit.SECONDS);
        }
        return currentCount < 1;
    }

}
