package com.joaovictorcostadev.pequi_short.repository
import com.joaovictorcostadev.pequi_short.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String): User?

    @Query("""
    select u
    from User u
    join fetch u.group g
    join fetch g.groupRules
    where u.email = :email
    """)
    fun findByEmailWithRules(email: String): User?
}