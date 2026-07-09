package com.joaovictorcostadev.pequi_short.repository
import com.joaovictorcostadev.pequi_short.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
}