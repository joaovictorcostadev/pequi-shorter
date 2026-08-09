package com.joaovictorcostadev.pequi_short.repository

import com.joaovictorcostadev.pequi_short.entity.UrlAccess
import org.springframework.data.jpa.repository.JpaRepository

interface UrlAccessRepository : JpaRepository<UrlAccess, Long> {
}