package com.samuca.todolist.auth

import com.samuca.todolist.dao.UsuarioDao
import com.samuca.todolist.model.Usuario
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class TokenAuthenticationFilter(private val tokenService: TokenService, private val repository: UsuarioDao) :
    OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val tokenFromHeader = getTokenFromHeader(request)
        val tokenValid = tokenService.isTokenValid(tokenFromHeader)
        if (tokenValid) {
            authenticate(tokenFromHeader)
        }
        filterChain.doFilter(request, response)
    }

    private fun authenticate(tokenFromHeader: String?) {
        val id = tokenService.getTokenId(tokenFromHeader)
        val optionalUsuario: Optional<Usuario> = repository.findById(id!!)
        if (optionalUsuario.isPresent()) {
            val usernamePasswordAuthenticationToken =
                UsernamePasswordAuthenticationToken(optionalUsuario.get(), null, null)
            SecurityContextHolder.getContext().authentication = usernamePasswordAuthenticationToken
        }
    }

    private fun getTokenFromHeader(request: HttpServletRequest): String? {
        val token = request.getHeader("Authorization")
        return if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            null
        } else token.substring(7)
    }
}