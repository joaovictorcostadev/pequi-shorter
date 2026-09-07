package com.joaovictorcostadev.pequi_short.repository

import com.joaovictorcostadev.pequi_short.entity.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
}