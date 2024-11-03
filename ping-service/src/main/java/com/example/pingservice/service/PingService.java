package com.example.pingservice.service;

import com.example.pingservice.config.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PingService {

    private final WebClient webClient;
    private final RateLimiter rateLimiter;

    public PingService(WebClient.Builder webClientBuilder, RateLimiter rateLimiter) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8288").build();
        this.rateLimiter = rateLimiter;
    }

    public Mono<String> ping() {
        return Mono.defer(() -> {
            // 检查是否可以处理请求
            if (rateLimiter.tryAcquire()) {
                return webClient.get()
                        .uri("/pong?message=Hello")
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> log.error("Request sent but Pong throttled it: {}", e.getMessage()))
                        .doOnSuccess(response -> log.info("Request sent & Pong responded: {}", response))
                        .onErrorReturn("Request throttled");
            } else {
                log.info("Request not sent as being rate limited");
                return Mono.just("Rate limited");
            }
        });
    }

}
