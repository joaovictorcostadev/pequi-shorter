package com.joaovictorcostadev.pequi_short.service

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Service
class RateLimitService {

    private val bucket: ConcurrentHashMap<String, Bucket> = ConcurrentHashMap()

    fun resolveBucket(key: String) : Bucket {
        return bucket.computeIfAbsent(key) {
            createNewBucket()
        }
    }

    private fun createNewBucket(): Bucket {
        val limit = Bandwidth
            .builder()
            .capacity(10)
            .refillGreedy(10, Duration.ofMinutes(1))
            .build()

        return Bucket.builder()
            .addLimit(limit)
            .build()
    }
}