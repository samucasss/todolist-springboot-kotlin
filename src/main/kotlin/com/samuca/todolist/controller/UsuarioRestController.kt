package com.samuca.todolist.controller

import com.samuca.todolist.dao.UsuarioDao
import com.samuca.todolist.model.Usuario
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
class UsuarioRestController(private val usuarioDao: UsuarioDao,
                            private val passwordEncoder: PasswordEncoder) {

    @PostMapping("/usuarios")
    fun save(@RequestBody @Valid usuario: Usuario?): Usuario? {
        if (usuario?.password != null) {
            val senha = passwordEncoder.encode(usuario.password)
            usuario.senha = senha

            val retorno: Usuario = usuarioDao.save(usuario)
            return retorno.copy(senha = null)
        }

        return null
    }

    @DeleteMapping("/usuario")
    fun delete(): String {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val usuario = authentication.getPrincipal() as Usuario

        usuarioDao.delete(usuario)

        return "OK"
    }
}