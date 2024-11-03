package com.example.pongservice

import com.example.pongservice.config.ThrottlingConfig
import com.example.pongservice.controller.PongController
import com.example.pongservice.service.PongService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import spock.lang.Specification

@WebFluxTest(PongController.class)
@ContextConfiguration(classes = PongServiceApplication.class)
@Import([ThrottlingConfig.class, PongService.class])
class PongTest extends Specification {

    @Autowired
    WebTestClient webTestClient

    //测试单次触发获取结果
    def "return 'World' when request is within limit"() {
        when:
        def response = webTestClient.get().uri("/pong?message=Hello").exchange()

        then:
        response.expectStatus().isOk()
        response.expectBody(String.class).isEqualTo("Hello World")
    }

    //测试同时触发
    def "return 429 when request exceeds limit"() {
        Thread.sleep(1000)

        when:
        def response1 = webTestClient.get().uri("/pong?message=Hello").exchange()
        def response2 = webTestClient.get().uri("/pong?message=Hello").exchange()

        then:
        response1.expectStatus().isOk()
        response2.expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
    }

    //测试同时触发
    def "return 429 when request exceeds limit, after 1s request is within limit return ok"() {
        Thread.sleep(1000)

        when:
        def response1 = webTestClient.get().uri("/pong?message=Hello").exchange()
        def response2 = webTestClient.get().uri("/pong?message=Hello").exchange()

        then:
        response1.expectStatus().isOk()
        response2.expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)

        and:
        Thread.sleep(1000)

        when:
        def response3 = webTestClient.get().uri("/pong?message=Hello").exchange()

        then:
        response3.expectBody(String.class).isEqualTo("Hello World")
    }

    def "return ok and after 0.5s return 429 when request exceeds limit"() {
        Thread.sleep(1000)

        when:
        def response1 = webTestClient.get().uri("/pong?message=Hello").exchange()

        then:
        response1.expectStatus().isOk()

        and:
        Thread.sleep(500)

        when:
        def response2 = webTestClient.get().uri("/pong?message=Hello").exchange()

        then:
        response2.expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
    }
}
