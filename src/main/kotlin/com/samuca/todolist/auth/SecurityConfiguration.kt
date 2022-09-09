package com.samuca.todolist.auth

import com.samuca.todolist.dao.UsuarioDao
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@EnableWebSecurity
@Configuration
class SecurityConfiguration(private val authenticationService: AuthenticationService,
                            private val tokenService: TokenService,
                            private val repository: UsuarioDao) : WebSecurityConfigurerAdapter() {

    @Bean
    @Throws(Exception::class)
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }

    //Configurations for authentication
    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(authenticationService).passwordEncoder(BCryptPasswordEncoder())
    }

    @Bean
    protected fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    //Configuration for authorization
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/tecnologias").permitAll()
            .antMatchers(HttpMethod.POST, "/auth/login").permitAll()
            .antMatchers(HttpMethod.POST, "/auth/register").permitAll()
            .anyRequest().authenticated()
            .and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().addFilterBefore(
                TokenAuthenticationFilter(tokenService!!, repository!!),
                UsernamePasswordAuthenticationFilter::class.java
            )
    }

    //Configuration for static resources
    @Throws(Exception::class)
    override fun configure(web: WebSecurity) {
    }
}