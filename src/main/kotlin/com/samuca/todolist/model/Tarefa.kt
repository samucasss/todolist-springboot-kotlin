package com.samuca.todolist.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Document(collection = "tarefas")
data class Tarefa(

    @Id
    val id: String?,

    @field:NotNull
    val data: LocalDate?,

    @field:NotBlank
    val nome: String?,

    val descricao: String?,

    @field:NotNull
    val done: Boolean?,

    var usuarioId: String?

)