package com.example.pongservice.service;

import com.example.pongservice.config.ThrottlingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PongService {

    private static final Logger log = LoggerFactory.getLogger(PongService.class);
    private final ThrottlingConfig throttlingConfig;

    public PongService(ThrottlingConfig throttlingConfig) {
        this.throttlingConfig = throttlingConfig;
    }

    public Mono<String> handleRequest(String message) {
        // 使用 defer() 延迟执行，确保每次调用都创建一个新的 Mono 对象
        return Mono.defer(() -> {
            // 检查请求是否符合节流设定
            if (throttlingConfig.canHandleRequest()) {
                log.info("Handling request: {}", message);
                return Mono.just(message + " " + "World");
            } else {
                return Mono.empty();
            }
        });
    }
}
