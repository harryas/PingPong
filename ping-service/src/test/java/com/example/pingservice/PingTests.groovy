package com.example.pingservice

import com.example.pingservice.config.RateLimiter
import com.example.pingservice.service.PingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.scheduling.TaskScheduler
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification
import reactor.test.StepVerifier

@SpringBootTest
@ContextConfiguration(classes = PingServiceApplication.class)
@Import([RateLimiter.class, PingService.class])
class PingTests extends Specification{

    @Autowired
    PingService pingService

    @MockBean
    TaskScheduler taskScheduler //模拟定时任务避免多次mono订阅触发

    def "once request"() {
        when:
        def result = pingService.ping()

        then:
        StepVerifier.create(result)
                .expectNext("Hello World")
                .verifyComplete()
    }

    def "throttling"() {
        Thread.sleep(1000)
        when:
        def result = pingService.ping()
        def result1 = pingService.ping()

        then:
        StepVerifier.create(result)
                .expectNext("Hello World")
                .verifyComplete()
        StepVerifier.create(result1)
                .expectNext("Request throttled")
                .verifyComplete()
    }

    def "limited"() {
        Thread.sleep(1000)
        when:
        def result = pingService.ping()
        def result1 = pingService.ping()
        def result2 = pingService.ping()

        then:
        StepVerifier.create(result)
                .expectNext("Hello World")
                .verifyComplete()
        StepVerifier.create(result1)
                .expectNext("Request throttled")
                .verifyComplete()
        StepVerifier.create(result2)
                .expectNext("Rate limited")
                .verifyComplete()
    }
}
