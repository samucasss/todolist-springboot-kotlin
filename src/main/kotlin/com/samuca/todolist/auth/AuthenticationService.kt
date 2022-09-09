package com.samuca.todolist.auth

import com.samuca.todolist.dao.UsuarioDao
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class AuthenticationService(private val usuarioDao: UsuarioDao) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val usuario = usuarioDao.findByEmail(username)
        if (usuario != null) {
            return usuario
        }
        throw UsernameNotFoundException("User not found")
    }
}