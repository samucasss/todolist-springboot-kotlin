package com.samuca.todolist.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Document(collection = "usuario")
data class Usuario(

    @Id
    val id: String?,

    @field:NotBlank
    val nome: String?,

    @field:NotBlank
    val email: String?,

    @field:NotBlank
    var senha: String?

) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return null
    }

    override fun getPassword(): String? {
        return senha
    }

    override fun getUsername(): String? {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}