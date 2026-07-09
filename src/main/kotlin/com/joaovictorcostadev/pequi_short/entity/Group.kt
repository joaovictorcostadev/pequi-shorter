package com.joaovictorcostadev.pequi_short.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "groups")
data class Group(

    @Column(unique = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column("name")
    val name: String,

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    val users: MutableList<User> = mutableListOf()
)

