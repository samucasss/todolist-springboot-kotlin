package com.samuca.todolist.controller

import com.samuca.todolist.dao.TarefaDao
import com.samuca.todolist.model.Tarefa
import com.samuca.todolist.model.Usuario
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.*
import javax.validation.Valid

@RestController
class TarefaRestController(private val tarefaDao: TarefaDao) {

    @GetMapping("/tarefas")
    fun findAll(@Valid @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") inicio: LocalDate,
                @Valid @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") fim: LocalDate): List<Tarefa> {

        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val (id) = authentication.getPrincipal() as Usuario

        if (id != null) {
            val inicioMenos1Dia: LocalDate = inicio.minusDays(1)
            val tarefaList: List<Tarefa> = tarefaDao.findAllByDataBetweenAndUsuarioId(inicioMenos1Dia, fim,
                id)

            return tarefaList
        }

        return arrayListOf()
    }

    @PostMapping("/tarefas")
    fun save(@RequestBody @Valid tarefa: Tarefa): Tarefa? {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val (id) = authentication.getPrincipal() as Usuario

        if (id != null) {
            tarefa.usuarioId = id
            val retorno: Tarefa = tarefaDao.save(tarefa)

            return retorno
        }

        return null
    }

    @DeleteMapping("/tarefas/{id}")
    fun delete(@PathVariable id: String): ResponseEntity<String> {
        val optionalTarefa: Optional<Tarefa> = tarefaDao.findById(id)
        if (!optionalTarefa.isPresent) {
            return ResponseEntity.badRequest().body("Nao existe tarefa para o id " + id)
        }

        tarefaDao.delete(optionalTarefa.get())
        return ResponseEntity.ok("OK");
    }

}