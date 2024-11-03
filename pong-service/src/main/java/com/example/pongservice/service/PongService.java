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
        // Use defer () to delay execution and ensure that a new Mono object is created with each call
        return Mono.defer(() -> {
            // Check if the request complies with the throttling settings
            if (throttlingConfig.canHandleRequest()) {
                log.info("Handling request: {}", message);
                return Mono.just(message + " " + "World");
            } else {
                return Mono.empty();
            }
        });
    }
}
