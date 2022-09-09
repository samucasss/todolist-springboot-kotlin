package com.samuca.todolist.auth

import com.samuca.todolist.dao.UsuarioDao
import com.samuca.todolist.model.Usuario
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
class AuthRestController(private val authenticationManager: AuthenticationManager,
                         private val tokenService: TokenService,
                         private val usuarioDao: UsuarioDao,
                         private val passwordEncoder: PasswordEncoder) {

    @PostMapping("/auth/register")
    fun register(@RequestBody @Valid usuario: Usuario): Usuario {
        if (usuario?.password != null) {
            val senha = passwordEncoder.encode(usuario.password)
            usuario.senha = senha
        }

        val retorno: Usuario = usuarioDao.save(usuario)
        return retorno.copy(senha = null)
    }

    @PostMapping("/auth/login")
    fun auth(@RequestBody login: Login): String {
        val usernamePasswordAuthenticationToken =
            UsernamePasswordAuthenticationToken(login.userName, login.password)

        val authentication: Authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken)
        return tokenService.generateToken(authentication)
    }

    @GetMapping("/auth/get")
    fun get(): Usuario {
        val authentication: Authentication = SecurityContextHolder.getContext().authentication
        val usuario = authentication.getPrincipal() as Usuario

        return usuario.copy(senha = null)
    }
}
