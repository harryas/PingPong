package com.example.pongservice.service;

import com.example.pongservice.config.ThrottlingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PongService {

    private static final Logger log = LoggerFactory.getLogger(PongService.class);
    private final ThrottlingConfig throttlingConfig;

    public PongService(ThrottlingConfig throttlingConfig) {
        this.throttlingConfig = throttlingConfig;
    }

    public Mono<String> handleRequest(String message) {
        // generate a unique request ID
        String requestId = UUID.randomUUID().toString();
        // Use defer () to delay execution and ensure that a new Mono object is created with each call
        return Mono.defer(() -> {
            log.info("Request {} received: {}", requestId, message);
            // Check if the request complies with the throttling settings
            if (throttlingConfig.canHandleRequest()) {
                log.info("Request {} handling: {}", requestId, message);
                return Mono.just(message + " " + "World");
            } else {
                log.warn("Request {} throttled: {}", requestId, message);
                return Mono.empty();
            }
        });
    }
}
