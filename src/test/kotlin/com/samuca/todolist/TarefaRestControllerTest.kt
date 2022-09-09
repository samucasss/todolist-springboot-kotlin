package com.samuca.todolist

import com.samuca.todolist.dao.TarefaDao
import com.samuca.todolist.model.Tarefa
import com.samuca.todolist.model.Usuario
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.*
import java.time.LocalDate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TarefaRestControllerTest(@LocalServerPort private val port: Int,
                               @Autowired private val restTemplate: TestRestTemplate,
                               @Autowired private val testUtil: TesteUtil,
                               @Autowired private val tarefaDao: TarefaDao) {

    @BeforeEach
    fun beforeEach() {
        testUtil.removeAllUsuarios()
        tarefaDao.deleteAll()
    }

    @Test
    fun testFindAllByPeriodo() {
        val usuario = testUtil.saveUsuario()
        saveTarefas(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/tarefas?inicio=2022-08-20&fim=2022-08-31"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val responseEntity = restTemplate.exchange(RequestEntity.get(url).headers(headers).build(),
            Array<Tarefa>::class.java
        )
        val tarefaList = responseEntity.body!!

        Assertions.assertNotNull(tarefaList)
        Assertions.assertEquals(2, tarefaList.size)
        val (id, data, nome, descricao, done, usuarioId) = tarefaList[0]
        val (id1, data1, nome1, descricao1, done1, usuarioId1) = tarefaList[1]

        Assertions.assertNotNull(id)
        Assertions.assertEquals(LocalDate.of(2022, 8, 26), data)
        Assertions.assertEquals("Tarefa 1", nome)
        Assertions.assertEquals("Descrição Tarefa 1", descricao)
        Assertions.assertEquals(false, done)
        Assertions.assertEquals(usuario.id, usuarioId)

        Assertions.assertNotNull(id1)
        Assertions.assertEquals(LocalDate.of(2022, 8, 27), data1)
        Assertions.assertEquals("Tarefa 2", nome1)
        Assertions.assertNull(descricao1)
        Assertions.assertEquals(true, done1)
        Assertions.assertEquals(usuario.id, usuarioId1)
    }

    @Test
    fun testFindAllByPeriodoSemInicio() {
        val usuario = testUtil.saveUsuario()
        saveTarefas(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/tarefas?fim=2022-08-31"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val result = restTemplate.exchange(RequestEntity.get(url).headers(headers).build(), String::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testFindAllByPeriodoSemFim() {
        val usuario = testUtil.saveUsuario()
        saveTarefas(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/tarefas?inicio=2022-08-31"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val result = restTemplate.exchange(RequestEntity.get(url).headers(headers).build(), String::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testFindAllByPeriodoSemAutenticacao() {
        val usuario = testUtil.saveUsuario()
        saveTarefas(usuario)

        val url = "http://localhost:$port/tarefas?inicio=2022-08-31"

        val result = restTemplate.exchange(RequestEntity.get(url).build(), String::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    @Test
    fun testSaveOk() {
        val (id) = testUtil.saveUsuario()
        val token = testUtil.login("samuca@gmail.com")
        val tarefa = Tarefa(null, LocalDate.of(2022, 8, 26), "Tarefa 1",
            "Descrição Tarefa 1", false, null)

        val url = "http://localhost:$port/tarefas"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(tarefa, headers)
        val result = restTemplate.postForEntity(url, request, Tarefa::class.java)
        val tarefaRest = result.body

        Assertions.assertNotNull(tarefaRest)
        Assertions.assertEquals(LocalDate.of(2022, 8, 26), tarefaRest!!.data)
        Assertions.assertNotNull(tarefaRest.id)
        Assertions.assertEquals("Tarefa 1", tarefaRest.nome)
        Assertions.assertEquals("Descrição Tarefa 1", tarefaRest.descricao)
        Assertions.assertEquals(false, tarefaRest.done)
        Assertions.assertEquals(id, tarefaRest.usuarioId)
    }

    @Test
    fun testSaveSemData() {
        val usuario = testUtil.saveUsuario()
        saveTarefa(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val tarefa = Tarefa(null, null, "Tarefa 1",
            "Descrição Tarefa 1", false, null)

        val url = "http://localhost:$port/tarefas"
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(tarefa, headers)
        val result = restTemplate.postForEntity(url, request, Tarefa::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testSaveSemNome() {
        val usuario = testUtil.saveUsuario()
        saveTarefa(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val tarefa = Tarefa(null, LocalDate.of(2022, 8, 26), "",
            "Descrição Tarefa 1", false, null)

        val url = "http://localhost:$port/tarefas"
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(tarefa, headers)
        val result = restTemplate.postForEntity(url, request, Tarefa::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testSaveSemAutenticacao() {
        val tarefa = Tarefa(null, LocalDate.of(2022, 8, 26), "Tarefa 1",
            "Descrição Tarefa 1", false, null)

        val url = "http://localhost:$port/tarefas"
        val request = HttpEntity(tarefa)
        val result = restTemplate.postForEntity(url, request, Tarefa::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    @Test
    fun testSaveAlteracaoOk() {
        val usuario = testUtil.saveUsuario()
        val (id) = saveTarefa(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val tarefa = Tarefa(id, LocalDate.of(2022, 8, 28), "Tarefa 1 alterada",
            "Descrição Tarefa 1 alterada", true, null)

        val url = "http://localhost:$port/tarefas"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(tarefa, headers)
        val result = restTemplate.postForEntity(url, request, Tarefa::class.java)
        val tarefaRest = result.body

        Assertions.assertNotNull(tarefaRest)
        Assertions.assertEquals(LocalDate.of(2022, 8, 28), tarefaRest!!.data)
        Assertions.assertEquals(id, tarefaRest.id)
        Assertions.assertEquals("Tarefa 1 alterada", tarefaRest.nome)
        Assertions.assertEquals("Descrição Tarefa 1 alterada", tarefaRest.descricao)
        Assertions.assertEquals(true, tarefaRest.done)
        Assertions.assertEquals(usuario.id, tarefaRest.usuarioId)
    }

    @Test
    fun testSaveAlteracaoSemData() {
        val usuario = testUtil.saveUsuario()
        val (id) = saveTarefa(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val tarefa = Tarefa(id, null, "Tarefa 1 alterada",
            "Descrição Tarefa 1 alterada", true, null)

        val url = "http://localhost:$port/tarefas"
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(tarefa, headers)
        val result = restTemplate.postForEntity(url, request, Tarefa::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testSaveAlteracaoSemNome() {
        val usuario = testUtil.saveUsuario()
        val (id) = saveTarefa(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val tarefa = Tarefa(id, LocalDate.of(2022, 8, 28), "",
            "Descrição Tarefa 1 alterada", true, null)

        val url = "http://localhost:$port/tarefas"
        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request = HttpEntity(tarefa, headers)
        val result = restTemplate.postForEntity(url, request, Tarefa::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testSaveAlteracaoSemAutenticacao() {
        val usuario = testUtil.saveUsuario()
        val (id) = saveTarefa(usuario)

        val tarefa = Tarefa(id, LocalDate.of(2022, 8, 28), "Tarefa 1 alterada",
            "Descrição Tarefa 1 alterada", true, null)

        val url = "http://localhost:$port/tarefas"
        val request = HttpEntity(tarefa)
        val result = restTemplate.postForEntity(url, request, Tarefa::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    @Test
    fun testDeleteOk() {
        val usuario = testUtil.saveUsuario()
        val (id) = saveTarefa(usuario)
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/tarefas/$id"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request: HttpEntity<String> = HttpEntity<String>(headers)
        val result = restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertNotNull("OK", result.body)
    }

    @Test
    fun testDeleteSemTarefa() {
        testUtil.saveUsuario()
        val token = testUtil.login("samuca@gmail.com")

        val url = "http://localhost:$port/tarefas/12345"

        val headers = HttpHeaders()
        headers.setBearerAuth(token)
        val request: HttpEntity<String> = HttpEntity<String>(headers)
        val result = restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun testDeleteNaoAutenticado() {
        val usuario = testUtil.saveUsuario()
        val (id) = saveTarefa(usuario)

        val url = "http://localhost:$port/tarefas/$id"

        val headers = HttpHeaders()
        val request: HttpEntity<*> = HttpEntity<Any?>(headers)
        val result = restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.statusCode)
    }

    private fun saveTarefa(usuario: Usuario): Tarefa {
        val tarefa = Tarefa(null, LocalDate.of(2022, 8, 26), "Tarefa 1",
            "Descrição Tarefa 1", false, usuario.id)

        val retorno: Tarefa = tarefaDao.save(tarefa)
        return retorno
    }

    private fun saveTarefas(usuario: Usuario) {
        val tarefa1 = Tarefa(null, LocalDate.of(2022, 8, 26), "Tarefa 1",
            "Descrição Tarefa 1", false, usuario.id)
        tarefaDao.save(tarefa1)

        val tarefa2 = Tarefa(null, LocalDate.of(2022, 8, 27), "Tarefa 2",
            null, true, usuario.id)
        tarefaDao.save(tarefa2)
    }

}