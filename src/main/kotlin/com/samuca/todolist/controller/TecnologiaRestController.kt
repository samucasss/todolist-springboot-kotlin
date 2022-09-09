package com.samuca.todolist.controller

import com.samuca.todolist.dao.TecnologiaDao
import com.samuca.todolist.model.Tecnologia
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TecnologiaRestController(private val tecnologiaDao: TecnologiaDao) {

    @GetMapping("/tecnologias")
    fun findAll(): List<Tecnologia> {
        val tecnologiaList: List<Tecnologia> = tecnologiaDao.findAll()
        return tecnologiaList
    }

}