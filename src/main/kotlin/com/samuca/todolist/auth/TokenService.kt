package com.samuca.todolist.auth

import com.samuca.todolist.model.Usuario
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*


@Service
class TokenService(@Value("\${jwt.expiration}") private val expiration: String,
                   @Value("\${jwt.secret}") private val secret: String) {

    fun generateToken(authentication: Authentication): String {
        val (id) = authentication.principal as Usuario
        val now = Date()
        val exp = Date(now.time + expiration.toLong())
        return Jwts.builder().setIssuer("IRS").setSubject(id).setIssuedAt(Date())
            .setExpiration(exp).signWith(SignatureAlgorithm.HS256, secret).compact()
    }

    fun isTokenValid(token: String?): Boolean {
        return try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getTokenId(token: String?): String {
        val body = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).body
        return body.subject
    }
}