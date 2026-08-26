package com.joaovictorcostadev.pequi_short.component

import org.springframework.stereotype.Component
import java.security.SecureRandom

@Component
class UniqueCodeGenerator {

    fun getUniqueCode(num: Int) : String {
        val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val random = SecureRandom()

        return buildString {
            repeat(num) {
                append(chars[random.nextInt(chars.length)])
            }
        }

    }
}