package com.samuca.todolist

import com.samuca.todolist.auth.TokenService
import com.samuca.todolist.dao.UsuarioDao
import com.samuca.todolist.model.Usuario
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component


@Component
class TesteUtil(private val usuarioDao: UsuarioDao,
                private val passwordEncoder: PasswordEncoder,
                private val tokenService: TokenService) {

    fun login(email: String): String {
        val usuario: Usuario = usuarioDao.findByEmail(email)

        val auth: Authentication = UsernamePasswordAuthenticationToken(usuario, null, null)
        val token: String = tokenService.generateToken(auth)

        return token
    }

    fun saveUsuario(): Usuario {
        val usuario = Usuario(null, "Samuel", "samuca@gmail.com",
            passwordEncoder.encode("samuca"))

        return usuarioDao.save(usuario)
    }

    fun removeAllUsuarios() {
        usuarioDao.deleteAll()
    }

}