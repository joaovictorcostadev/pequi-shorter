package com.joaovictorcostadev.pequi_short.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.Instant

@Entity
data class Url(

    @Column(unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    @Column(unique = true, nullable = false)
    val name: String,

    @Column(name="external_url", nullable = false)
    val externalUrl: String,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    val updatedAt: Instant,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user:User
)