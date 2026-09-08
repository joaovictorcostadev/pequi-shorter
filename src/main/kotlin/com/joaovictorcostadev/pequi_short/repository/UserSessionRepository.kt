package com.joaovictorcostadev.pequi_short.repository

import com.joaovictorcostadev.pequi_short.entity.UserSession
import org.springframework.data.jpa.repository.JpaRepository

interface UserSessionRepository : JpaRepository<UserSession, Long> {
}