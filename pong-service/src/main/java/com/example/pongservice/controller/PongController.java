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
        return pongService.handleRequest(message) // call the handleRequest method of PongService
                .map(ResponseEntity::ok) // 200 OK, return the result
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Throttled")); // empty, return 429 Too Many Requests
    }
}
