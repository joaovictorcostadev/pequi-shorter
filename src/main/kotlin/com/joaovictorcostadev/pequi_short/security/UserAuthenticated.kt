package com.joaovictorcostadev.pequi_short.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.lang.IllegalStateException

@Component
class UserAuthenticated {

     fun getUsernameLogged() : String {
        val authentication = SecurityContextHolder.getContext().authentication
        if(authentication != null && authentication.isAuthenticated) {
            val principal = authentication.principal

            if(principal is UserDetails) {
                return principal.username
            }

            return principal.toString()
        }

        throw IllegalStateException("Not user Logged")
    }

}