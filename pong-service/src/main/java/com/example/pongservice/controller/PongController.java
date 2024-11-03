package com.example.pongservice.controller;

import com.example.pongservice.service.PongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pong")
public class PongController {

    private final PongService pongService;

    public PongController(PongService pongService) {
        this.pongService = pongService;
    }

    @GetMapping
    public Mono<ResponseEntity<String>> pong(@RequestParam String message) {
        return pongService.handleRequest(message) // 调用PongService 的 handleRequest 方法
                .map(ResponseEntity::ok) // 如果有响应，则返回200 OK
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Throttled")); // 如果没有响应，则返回429 Too Many Requests
    }
}
