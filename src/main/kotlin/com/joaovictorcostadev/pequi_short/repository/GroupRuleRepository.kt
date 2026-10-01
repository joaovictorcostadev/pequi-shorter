package com.joaovictorcostadev.pequi_short.repository

import com.joaovictorcostadev.pequi_short.entity.GroupRule
import org.springframework.data.jpa.repository.JpaRepository
import com.joaovictorcostadev.pequi_short.entity.Group
import com.joaovictorcostadev.pequi_short.entity.Rule

interface GroupRuleRepository : JpaRepository<GroupRule, Long> {
    fun existsByGroupAndRule(group: Group, rule: Rule): Boolean
}