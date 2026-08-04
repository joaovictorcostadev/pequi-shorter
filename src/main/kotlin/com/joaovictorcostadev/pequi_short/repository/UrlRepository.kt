package com.joaovictorcostadev.pequi_short.repository

import com.joaovictorcostadev.pequi_short.entity.Url
import org.springframework.data.jpa.repository.JpaRepository

interface UrlRepository : JpaRepository<Url, Long> {

    fun findByName(name: String) : Url?
}