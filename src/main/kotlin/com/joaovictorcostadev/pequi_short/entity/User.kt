package com.joaovictorcostadev.pequi_short.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import kotlin.time.Clock
import java.time.Instant
@Entity
@Table(name = "users")
data class User(

    @Column(unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(unique = true)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(name = "group_id", nullable = false)
    val groupId: Int,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    val updatedAt: Instant?

)
