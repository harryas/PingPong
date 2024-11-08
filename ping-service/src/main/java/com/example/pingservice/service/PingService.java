package com.example.pingservice.service;

import com.example.pingservice.config.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
            // generate a unique request ID
            String requestId = UUID.randomUUID().toString();
            // check if we can send a request
            if (rateLimiter.tryAcquire()) {
                String message = "Hello";
                log.info("Request {} sent message: {}", requestId, message);
                // send the request to the pong service
                return webClient.get()
                        .uri("/pong?message=" + message)
                        .retrieve()
                        .bodyToMono(String.class)
                        .doOnError(e -> log.error("Request {} sent but Pong throttled it: {}", requestId, e.getMessage()))
                        .doOnSuccess(response -> log.info("Request {} sent & Pong responded: {}", requestId, response))
                        .onErrorReturn("Request throttled");
            } else {
                log.info("Request {} not sent as being rate limited", requestId);
                return Mono.just("Rate limited");
            }
        });
    }

}
