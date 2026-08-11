package com.joaovictorcostadev.pequi_short.service

import com.joaovictorcostadev.pequi_short.entity.GroupRule
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
        val user =userRepository.findByEmailWithRules(username) ?: throw UsernameNotFoundException("User not found")
        val rules: List<GroupRule> = user.group.groupRules
        val authorities: List<SimpleGrantedAuthority> = rules.map { SimpleGrantedAuthority(it.rule.name) }
        return User
            .builder()
            .username(user.email)
            .password(user.password)
            .authorities(authorities)
            .build()
    }
}