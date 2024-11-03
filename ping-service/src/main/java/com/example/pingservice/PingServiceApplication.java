package com.example.pingservice;

import com.example.pingservice.service.PingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class PingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PingServiceApplication.class, args);
	}

	@Autowired
	private PingService pingService;

	@Scheduled(fixedRateString = "${ping.service.interval:400}")
//	@Scheduled(fixedRate = 1000)
	public void pingPong() {
		//订阅触发请求
		pingService.ping().subscribe();
	}


}
