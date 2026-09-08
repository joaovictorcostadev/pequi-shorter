package com.joaovictorcostadev.pequi_short.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "refresh_tokens",
    indexes = [Index(name = "idx_refresh_token", columnList = "token", unique = true)]
)
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "token", nullable = false, unique = true)
    var token: String,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "revoke_at", nullable = false)
    var revokeAt: Instant
) {
    fun isExpired(): Boolean = Instant.now().isAfter(revokeAt)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RefreshToken) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 31
}