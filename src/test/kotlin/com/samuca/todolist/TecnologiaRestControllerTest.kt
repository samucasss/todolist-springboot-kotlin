package com.samuca.todolist

import com.samuca.todolist.dao.TecnologiaDao
import com.samuca.todolist.model.Tecnologia
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort

import org.junit.jupiter.api.Assertions
import org.springframework.http.ResponseEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TecnologiaRestControllerTest(@LocalServerPort private val port: Int,
                                   @Autowired private val restTemplate: TestRestTemplate,
                                   @Autowired private val tecnologiaDao: TecnologiaDao) {

    @Test
    fun testFindAll() {
        tecnologiaDao.deleteAll()
        saveTecnologias()

        val url = "http://localhost:$port/tecnologias"
        val result: ResponseEntity<Array<Tecnologia>> = restTemplate.getForEntity(url,
            Array<Tecnologia>::class.java)

        val tecnologiaList = result.body;
        Assertions.assertTrue(tecnologiaList!!.size > 0)
        Assertions.assertEquals(4, tecnologiaList.size)
    }

    private fun saveTecnologias() {
        tecnologiaDao.save(Tecnologia(null, "Node Js", "Backend"))
        tecnologiaDao.save(Tecnologia(null, "Java", "Backend"))
        tecnologiaDao.save(Tecnologia(null, "Angular", "Frontend"))
        tecnologiaDao.save(Tecnologia(null, "Vue", "Frontend"))
    }
}
