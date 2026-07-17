package com.joaovictorcostadev.pequi_short.repository

import com.joaovictorcostadev.pequi_short.entity.Rule
import org.springframework.data.jpa.repository.JpaRepository

interface RuleRepository : JpaRepository<Rule, Long> {
}