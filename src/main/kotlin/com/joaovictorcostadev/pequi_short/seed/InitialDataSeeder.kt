package com.joaovictorcostadev.pequi_short.seed

import com.joaovictorcostadev.pequi_short.entity.Group
import com.joaovictorcostadev.pequi_short.entity.GroupRule
import com.joaovictorcostadev.pequi_short.entity.Rule
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import com.joaovictorcostadev.pequi_short.repository.GroupRuleRepository
import com.joaovictorcostadev.pequi_short.repository.RuleRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.DependsOn
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@DependsOn("flyway")
class InitialDataSeeder(
    private val groupRepository: GroupRepository,
    private val ruleRepository: RuleRepository,
    private val groupRuleRepository: GroupRuleRepository,
) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(InitialDataSeeder::class.java)

    @Transactional
    override fun run(vararg args: String) {

        val groupAdmin = findOrCreateGroup("ADMIN")
        val groupUser = findOrCreateGroup("USER")

        val ruleNames = listOf(
            "URL_GET", "URL_CREATE", "URL_UPDATE", "URL_DELETE",
            "USER_GET", "USER_UPDATE", "USER_DELETE", "USER_CREATE",
            "ADMIN_USER_GET", "ADMIN_USER_UPDATE", "ADMIN_USER_DELETE", "ADMIN_USER_CREATE",
            "GROUP_GET", "GROUP_GET_ALL", "GROUP_CREATE", "GROUP_UPDATE", "GROUP_DELETE",
            "RULE_GET", "RULE_GET_ALL", "RULE_CREATE", "RULE_UPDATE", "RULE_DELETE",
        )

        val rules = ruleNames.map { findOrCreateRule(it) }

        val userRuleNames = setOf(
            "URL_GET", "URL_CREATE", "URL_UPDATE",
            "USER_GET", "USER_UPDATE", "USER_DELETE",
        )

        val adminRuleNames = setOf(
            "ADMIN_USER_GET", "ADMIN_USER_CREATE", "ADMIN_USER_UPDATE", "ADMIN_USER_DELETE",
            "URL_GET", "URL_CREATE", "URL_UPDATE", "URL_DELETE",
            "GROUP_GET", "GROUP_GET_ALL", "GROUP_CREATE", "GROUP_UPDATE", "GROUP_DELETE",
            "RULE_GET", "RULE_GET_ALL", "RULE_CREATE", "RULE_UPDATE", "RULE_DELETE",
        )

        for (rule in rules) {
            if (rule.name in userRuleNames) {
                assignRuleToGroup(rule, groupUser)
            }
            if (rule.name in adminRuleNames) {
                assignRuleToGroup(rule, groupAdmin)
            }
        }

        logger.info("Initial data seeding completed.")
    }

    private fun findOrCreateGroup(name: String): Group {
        return groupRepository.findByName(name) ?: run {
            logger.info("Creating group: $name")
            groupRepository.save(Group(name = name))
        }
    }

    private fun findOrCreateRule(name: String): Rule {
        return ruleRepository.findByName(name) ?: run {
            logger.info("Creating rule: $name")
            ruleRepository.save(Rule(name = name))
        }
    }

    private fun assignRuleToGroup(rule: Rule, group: Group) {
        if (!groupRuleRepository.existsByGroupAndRule(group, rule)) {
            logger.info("Assigning rule '${rule.name}' to group '${group.name}'")
            groupRuleRepository.save(GroupRule(rule = rule, group = group))
        }
    }
}