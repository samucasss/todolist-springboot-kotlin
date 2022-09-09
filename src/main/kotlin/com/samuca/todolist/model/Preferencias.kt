package com.samuca.todolist.model

import org.springframework.data.annotation.Id
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class Preferencias(

    @Id
    var id: String?,

    @field:NotBlank
    val tipoFiltro: String?,

    @field:NotNull
    val done: Boolean?,

    var usuarioId: String?
) {

    constructor(tipoFiltro: String?, done: Boolean?) : this(null, tipoFiltro, done, null)
}