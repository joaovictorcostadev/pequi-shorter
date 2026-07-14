package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user =userRepository.findByEmail(username) ?: throw UsernameNotFoundException("User not found")
        return User
            .builder()
            .username(user.email)
            .password(user.password)
            .authorities(emptyList())
            .build()
    }
}