package com.samuca.todolist

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort

import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloRestControllerTest(@LocalServerPort private val port: Int,
                              @Autowired private val restTemplate: TestRestTemplate) {

    @Test
    fun testHello() {
        val retorno = restTemplate.getForObject("http://localhost:$port/", String::class.java)
        assertThat(retorno == "Hello world com spring boot com kotlin")
    }
}
