package com.example.pingservice.config;

import com.example.pingservice.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PingSchedule {


    @Autowired
    private PingService pingService;

    @Scheduled(fixedRateString = "${ping.service.interval:400}")
    public void pingPong() {
        // subscribe to the ping service
        pingService.ping().subscribe();
    }
}
