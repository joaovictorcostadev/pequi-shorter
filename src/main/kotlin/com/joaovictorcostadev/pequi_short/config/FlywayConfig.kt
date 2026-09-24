package com.joaovictorcostadev.pequi_short.config

import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class FlywayConfig(private val dataSource: DataSource) {

    private val log = LoggerFactory.getLogger(FlywayConfig::class.java)

    @Bean(name = ["flyway"])
    fun flyway(): Flyway {
        log.info(">>>> [FLYWAY] Iniciando configuração e execução das migrações <<<<")

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .baselineOnMigrate(false)
            .load()

        val result = flyway.migrate()
        log.info(">>>> [FLYWAY] Migrações executadas: ${result.migrationsExecuted} <<<<")

        return flyway
    }
}
