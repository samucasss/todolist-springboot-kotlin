package com.samuca.todolist

import com.samuca.todolist.dao.PreferenciasDao
import com.samuca.todolist.model.Preferencias
import com.samuca.todolist.model.Usuario
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PreferenciasRestControllerTest(@LocalServerPort private val port: Int,
                                     @Autowired private val restTemplate: TestRestTemplate,
                                     @Autowired private val testUtil: TesteUtil,
                                     @Autowired private val preferenciasDao: PreferenciasDao) {


    @BeforeEach
    fun beforeEach() {
        testUtil.removeAllUsuarios()
        preferenciasDao.deleteAll()
    }

    @Test
    fun testSaveOk() {
        testUtil.saveUsuario()
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/preferencias"

        val preferencias = Preferencias("T", false)

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(preferencias, headers)
        val result = restTemplate.postForEntity(url, request, Preferencias::class.java)
        val preferenciasRest = result.body

        Assertions.assertNotNull(preferenciasRest)
        Assertions.assertNotNull(preferenciasRest?.id)
        Assertions.assertEquals("T", preferenciasRest?.tipoFiltro)
        Assertions.assertEquals(false, preferenciasRest?.done)
    }

    @Test
    fun testSaveSemTipoFiltro() {
        testUtil.saveUsuario()
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/preferencias"

        val preferencias = Preferencias("", false)

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(preferencias, headers)
        val result = restTemplate.postForEntity(url, request, Preferencias::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testSaveSemDone() {
        testUtil.saveUsuario()
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/preferencias"

        val preferencias = Preferencias("T", null)

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(preferencias, headers)
        val result = restTemplate.postForEntity(url, request, Preferencias::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testSaveSemAutenticacao() {
        testUtil.saveUsuario()

        val url = "http://localhost:$port/preferencias"

        val preferencias = Preferencias("T", false)

        val request = HttpEntity(preferencias)
        val result = restTemplate.postForEntity(url, request, Preferencias::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    @Test
    fun testSaveAlteracaoOk() {
        val usuario = testUtil.saveUsuario()
        savePreferencias(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/preferencias"

        val preferencias = Preferencias("H", true)

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(preferencias, headers)
        val result = restTemplate.postForEntity(url, request, Preferencias::class.java)
        val preferenciasRest = result.body

        val count = preferenciasDao.countByUsuarioId(usuario.id!!)

        Assertions.assertNotNull(preferenciasRest)
        Assertions.assertNotNull(preferenciasRest!!.id)
        Assertions.assertEquals(1, count)
        Assertions.assertEquals("H", preferenciasRest.tipoFiltro)
        Assertions.assertEquals(true, preferenciasRest.done)
    }

    @Test
    fun testSaveAlteracaoSemTipoFiltro() {
        val usuario = testUtil.saveUsuario()
        savePreferencias(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/preferencias"

        val preferencias = Preferencias("", true)

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(preferencias, headers)
        val result = restTemplate.postForEntity(url, request, Preferencias::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testSaveAlteracaoSemDone() {
        val usuario = testUtil.saveUsuario()
        savePreferencias(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/preferencias"

        val preferencias = Preferencias("H", null)

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(preferencias, headers)
        val result = restTemplate.postForEntity(url, request, Preferencias::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testSaveAlteracaoSemAutenticacao() {
        val usuario = testUtil.saveUsuario()
        savePreferencias(usuario)

        val url = "http://localhost:$port/preferencias"

        val preferencias = Preferencias("H", true)

        val headers = HttpHeaders()
        val request = HttpEntity(preferencias, headers)
        val result = restTemplate.postForEntity(url, request, Preferencias::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    @Test
    fun testGetOk() {
        val usuario = testUtil.saveUsuario()
        savePreferencias(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/preferencia"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val responseEntity = restTemplate.exchange(RequestEntity.get(url).headers(headers).build(),
            Preferencias::class.java)
        val preferenciasRest = responseEntity.body

        Assertions.assertNotNull(preferenciasRest)
        Assertions.assertNotNull(preferenciasRest!!.id)
        Assertions.assertEquals("T", preferenciasRest.tipoFiltro)
        Assertions.assertEquals(false, preferenciasRest.done)
    }

    @Test
    fun testGetSemAutenticacao() {
        val usuario = testUtil.saveUsuario()
        savePreferencias(usuario)

        val url = "http://localhost:$port/preferencia"
        val result = restTemplate.exchange(RequestEntity.get(url).build(), Preferencias::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    @Test
    fun testDeleteOk() {
        val usuario = testUtil.saveUsuario()
        savePreferencias(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/preferencia"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request: HttpEntity<String> = HttpEntity<String>(headers)
        val result = restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertNotNull("OK", result.body)
    }

    @Test
    fun testDeleteSemPreferencias() {
        testUtil.saveUsuario()
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/preferencia"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request: HttpEntity<String> = HttpEntity<String>(headers)
        val result = restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testDeleteNaoAutenticado() {
        val usuario = testUtil.saveUsuario()
        savePreferencias(usuario)

        val url = "http://localhost:$port/preferencia"

        val headers = HttpHeaders()
        val request: HttpEntity<String> = HttpEntity<String>(headers)
        val result = restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    private fun savePreferencias(usuario: Usuario) {
        val preferencias = Preferencias(null, "T", false, usuario.id)
        preferenciasDao.save(preferencias)
    }


}