package com.samuca.todolist.controller

import com.samuca.todolist.dao.PreferenciasDao
import com.samuca.todolist.model.Preferencias
import com.samuca.todolist.model.Usuario
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
class PreferenciasRestController(private val preferenciasDao: PreferenciasDao) {

    @GetMapping("/preferencia")
    fun get(): Preferencias? {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val (id) = authentication.getPrincipal() as Usuario

        if (id != null) {
            val preferencias: Preferencias? = preferenciasDao.findByUsuarioId(id)
            return preferencias
        }

        return null
    }

    @PostMapping("/preferencias")
    fun save(@RequestBody @Valid preferencias: Preferencias): Preferencias? {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val (id) = authentication.getPrincipal() as Usuario

        if (id != null) {
            val preferenciasExistente: Preferencias? = preferenciasDao.findByUsuarioId(id)
            if (preferenciasExistente != null) {
                preferencias.id = preferenciasExistente.id
            }

            preferencias.usuarioId = id

            val retorno: Preferencias = preferenciasDao.save(preferencias)
            return retorno
        }

        return null
    }

    @DeleteMapping("/preferencia")
    fun delete(): ResponseEntity<String> {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val (id) = authentication.getPrincipal() as Usuario

        if (id != null) {
            val preferencias: Preferencias? = preferenciasDao.findByUsuarioId(id)
            if (preferencias == null) {
                return ResponseEntity.badRequest().body("Nao existe preferencias para o usuario")
            }

            preferenciasDao.delete(preferencias)

            return ResponseEntity.ok("OK")
        }

        return ResponseEntity.badRequest().body("Nao existe tarefa para o usuario logado ")
    }

}