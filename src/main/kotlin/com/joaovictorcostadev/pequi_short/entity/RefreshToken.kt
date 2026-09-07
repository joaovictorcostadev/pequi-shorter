package com.joaovictorcostadev.pequi_short.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(

    @Column(unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val userId: User,

    @Column(name = "token", nullable = false)
    var token: String,

    @Column(name = "created_at")
    var createdAt: Instant = Instant.now(),


    @Column(name = "revoke_at")
    val revokeAt: Instant

)
