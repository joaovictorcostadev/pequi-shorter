package com.joaovictorcostadev.pequi_short.seed

import com.joaovictorcostadev.pequi_short.dto.user.UserRequestDto
import com.joaovictorcostadev.pequi_short.entity.Group
import com.joaovictorcostadev.pequi_short.entity.GroupRule
import com.joaovictorcostadev.pequi_short.entity.Rule
import com.joaovictorcostadev.pequi_short.entity.User
import com.joaovictorcostadev.pequi_short.repository.GroupRepository
import com.joaovictorcostadev.pequi_short.repository.GroupRuleRepository
import com.joaovictorcostadev.pequi_short.repository.RuleRepository
import com.joaovictorcostadev.pequi_short.repository.UserRepository
import com.joaovictorcostadev.pequi_short.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class InitialDataSeeder(
    private val groupRepository: GroupRepository,
    private val ruleRepository: RuleRepository,
    private val groupRuleRepository: GroupRuleRepository,
    private val userService: UserService
) : CommandLineRunner {

    override fun run(vararg args: String) {

        val groupAdmin: Group = groupRepository.save(Group(name = "ADMIN"))
        val groupUser: Group = groupRepository.save(Group(name = "USER"))

        val rules: List<Rule> = ruleRepository.saveAll<Rule>(listOf<Rule>(
            Rule(name = "URL_GET"),
            Rule(name = "URL_CREATE"),
            Rule(name = "URL_UPDATE"),
            Rule(name = "URL_DELETE"),
            Rule(name = "USER_GET"),
            Rule(name = "USER_UPDATE"),
            Rule(name = "USER_DELETE"),
            Rule(name = "USER_CREATE"),
            Rule(name = "GROUP_GET"),
            Rule(name = "GROUP_GET_ALL"),
            Rule(name = "GROUP_CREATE"),
            Rule(name = "GROUP_UPDATE"),
            Rule(name = "GROUP_DELETE"),
            ))

        val  userRules: List<String> = listOf<String>(
            "URL_GET",
            "URL_CREATE",
            "URL_UPDATE",
            "USER_GET",
            "USER_UPDATE",
            "USER_DELETE",
            )

        val adminRules: List<String> = listOf(
            "USER_GET",
            "USER_CREATE",
            "USER_UPDATE",
            "USER_DELETE",
            "URL_GET",
            "URL_CREATE",
            "URL_UPDATE",
            "URL_DELETE",
            "GROUP_GET",
            "GROUP_GET_ALL",
            "GROUP_CREATE",
            "GROUP_UPDATE",
            "GROUP_DELETE",
        )

        for (rule in rules) {
            if(userRules.contains(rule.name)) {
                groupRuleRepository.save(GroupRule(rule = rule, group = groupUser))
            }
            if(adminRules.contains(rule.name)) {
                groupRuleRepository.save(GroupRule(rule = rule, group = groupAdmin))
            }
        }

        userService.save(UserRequestDto(name = "Joao Victor", email = "user@gmail.com", "user123", groupId = groupUser.id!!))
        userService.save(UserRequestDto(name = "Joao Victor", email = "admin@gmail.com", "admin123", groupId = groupAdmin.id!!))

    }
}