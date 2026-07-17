package com.joaovictorcostadev.pequi_short.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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

    @ManyToOne(
        fetch = FetchType.EAGER,
        optional = false,
        )
    @JoinColumn(
        name = "group_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_user_group")
    )
    val group: Group,

    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at")
    val updatedAt: Instant?

)
