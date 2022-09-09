package com.samuca.todolist

import com.samuca.todolist.model.Usuario
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsuarioRestControllerTest(@LocalServerPort private val port: Int,
                                @Autowired private val restTemplate: TestRestTemplate,
                                @Autowired private val testUtil: TesteUtil) {

    @BeforeEach
    fun beforeEach() {
        testUtil.removeAllUsuarios()
        testUtil.saveUsuario()
    }

    @Test
    fun testAlterarUsuarioOk() {
        val url = "http://localhost:$port/usuarios"

        val token = testUtil.login("samuca@gmail.com")

        val usuario = Usuario(null, "Samuel Santos", "samuca.santos@gmail.com","samuca")

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(usuario, headers)
        val result = restTemplate.postForEntity(url, request, Usuario::class.java)
        val usuarioRest = result.body

        Assertions.assertNotNull(usuarioRest)
        Assertions.assertNotNull(usuarioRest!!.id)
        Assertions.assertNull(usuarioRest.senha)
        Assertions.assertEquals("Samuel Santos", usuarioRest.nome)
        Assertions.assertEquals("samuca.santos@gmail.com", usuarioRest.email)
    }

    @Test
    fun testAlterarUsuarioSemNome() {
        val url = "http://localhost:$port/usuarios"

        val token = testUtil.login("samuca@gmail.com")

        val usuario = Usuario(null, "", "samuca.santos@gmail.com","samuca")

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(usuario, headers)
        val result = restTemplate.postForEntity(url, request, Usuario::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testAlterarUsuarioSemSemEmail() {
        val url = "http://localhost:$port/usuarios"

        val token = testUtil.login("samuca@gmail.com")

        val usuario = Usuario(null, "Samuel Santos", "","samuca")

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(usuario, headers)
        val result = restTemplate.postForEntity(url, request, Usuario::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testAlterarUsuarioSemSemSenha() {
        val url = "http://localhost:$port/usuarios"

        val token = testUtil.login("samuca@gmail.com")

        val usuario = Usuario(null, "Samuel Santos", "samuca.santos@gmail.com","")

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(usuario, headers)
        val result = restTemplate.postForEntity(url, request, Usuario::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testAlterarUsuarioNaoAutenticado() {
        val url = "http://localhost:$port/usuarios"

        val usuario = Usuario(null, "Samuel", "samuca@gmail.com","samuca")

        val request = HttpEntity(usuario)
        val result = restTemplate.postForEntity(url, request, Usuario::class.java)
        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    @Test
    fun testDeleteOk() {
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/usuario"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request: HttpEntity<String> = HttpEntity<String>(headers)
        val result = restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertNotNull("OK", result.body)
    }

    @Test
    fun testDeleteUsuarioNaoAutenticado() {
        val url = "http://localhost:$port/usuario"

        val headers = HttpHeaders()
        val request: HttpEntity<String> = HttpEntity<String>(headers)
        val result = restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

}