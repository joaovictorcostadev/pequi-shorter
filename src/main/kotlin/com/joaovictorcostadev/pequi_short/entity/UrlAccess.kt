package com.joaovictorcostadev.pequi_short.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.Instant

@Entity
data class UrlAccess (
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id")
    val url: Url,

    @Column(name = "ip", nullable = false)
    val ip: String,

    @Column(name = "country")
    val country: String,

    @Column(name = "State")
    val state: String,

    @Column(name = "city")
    val city: String,

    @Column(name = "device_type")
    val deviceType: String? = null,

    @Column(name = "operating_system")
    val operatingSystem: String? = null,

    @Column(name = "browser")
    val browser: String? = null,

    @Column(name = "user_agent")
    val userAgent: String? = null,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    val updatedAt: Instant?

)