package com.samuca.todolist

import com.samuca.todolist.auth.Login
import com.samuca.todolist.model.Usuario
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthRestControllerTest(@LocalServerPort private val port: Int,
                             @Autowired private val restTemplate: TestRestTemplate,
                             @Autowired private val testUtil: TesteUtil) {


    @BeforeEach
    fun removeAllUsuariosBefore() {
        testUtil.removeAllUsuarios()
    }

    @Test
    fun testRegisterOk() {
        val url = "http://localhost:$port/auth/register"

        val usuario = Usuario(null, "Samuel", "samuca@gmail.com","samuca")

        val usuarioRest = restTemplate.postForObject(url, usuario, Usuario::class.java)

        Assertions.assertNotNull(usuarioRest)
        Assertions.assertNotNull(usuarioRest.id)
        Assertions.assertNull(usuarioRest.senha)
        Assertions.assertEquals("Samuel", usuarioRest.nome)
        Assertions.assertEquals("samuca@gmail.com", usuarioRest.email)
    }

    @Test
    fun testRegisterNomeNaoPreenchido() {
        val url = "http://localhost:$port/auth/register"

        val usuario = Usuario(null, "", "samuca@gmail.com","samuca")

        val response = restTemplate.postForEntity(url, usuario, String::class.java)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testRegisterEmailNaoPreenchido() {
        val url = "http://localhost:$port/auth/register"

        val usuario = Usuario(null, "Samuel", "","samuca")

        val response = restTemplate.postForEntity(url, usuario, String::class.java)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testRegisterSenhaNaoPreenchida() {
        val url = "http://localhost:$port/auth/register"

        val usuario = Usuario(null, "Samuel", "samuca@gmail.com","")

        val response = restTemplate.postForEntity(url, usuario, String::class.java)
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun testLoginOk() {
        testUtil.saveUsuario()

        val url = "http://localhost:$port/auth/login"

        val credentials = Login("samuca@gmail.com", "samuca")
        val token = restTemplate.postForObject(url, credentials, String::class.java)

        Assertions.assertNotNull(token)
    }

    @Test
    fun testLoginSenhaIncorreta() {
        testUtil.saveUsuario()

        val url = "http://localhost:$port/auth/login"

        val credentials = Login("samuca@gmail.com", "samuca123")
        val response = restTemplate.postForEntity(url, credentials, String::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun testLoginEmailInexistente() {
        testUtil.saveUsuario()

        val url = "http://localhost:$port/auth/login"

        val credentials = Login("samucasss@gmail.com", "samuca")
        val response = restTemplate.postForEntity(url, credentials, String::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun testLoginCamposNaoPreenchidos() {
        testUtil.saveUsuario()

        val url = "http://localhost:$port/auth/login"
        val credentials = Login("", "")
        val response = restTemplate.postForEntity(url, credentials, String::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun testGetOk() {
        testUtil.saveUsuario()
        val token: String = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/auth/get"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val responseEntity = restTemplate.exchange(RequestEntity.get(url).headers(headers).build(),
            Usuario::class.java)
        val usuarioRest = responseEntity.body

        Assertions.assertNotNull(usuarioRest)
        Assertions.assertNotNull(usuarioRest!!.id)
        Assertions.assertNull(usuarioRest.senha)
        Assertions.assertEquals("Samuel", usuarioRest.nome)
        Assertions.assertEquals("samuca@gmail.com", usuarioRest.email)
    }

    @Test
    fun testGetNaoAutenticado() {
        testUtil.saveUsuario()

        val url = "http://localhost:$port/auth/get"

        val response = restTemplate.getForEntity(url, String::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }
}
