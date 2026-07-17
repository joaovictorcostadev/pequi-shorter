package com.joaovictorcostadev.pequi_short.repository

import com.joaovictorcostadev.pequi_short.entity.GroupRule
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRuleRepository : JpaRepository<GroupRule, Long> {
}