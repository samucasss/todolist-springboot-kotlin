package com.samuca.todolist.dao

import com.samuca.todolist.model.Usuario
import org.springframework.data.mongodb.repository.MongoRepository

interface UsuarioDao: MongoRepository<Usuario, String> {

    fun findByEmail(email: String): Usuario
}