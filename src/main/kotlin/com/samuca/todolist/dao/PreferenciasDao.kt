package com.samuca.todolist.dao

import com.samuca.todolist.model.Preferencias
import org.springframework.data.mongodb.repository.MongoRepository

interface PreferenciasDao: MongoRepository<Preferencias, String> {

    fun findByUsuarioId(usuarioId: String): Preferencias?
    fun countByUsuarioId(usuarioId: String): Long

}