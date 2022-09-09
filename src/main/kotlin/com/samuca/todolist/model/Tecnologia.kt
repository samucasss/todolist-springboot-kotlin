package com.samuca.todolist.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "tecnologias")
data class Tecnologia (
    @Id
    val id: String?,

    val nome: String,
    val tipo: String
)