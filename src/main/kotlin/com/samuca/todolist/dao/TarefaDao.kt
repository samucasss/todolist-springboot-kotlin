package com.samuca.todolist.dao

import com.samuca.todolist.model.Tarefa
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDate

interface TarefaDao: MongoRepository<Tarefa, String> {

    fun findAllByDataBetweenAndUsuarioId(inicio: LocalDate, fim: LocalDate, usuarioId: String): List<Tarefa>
}