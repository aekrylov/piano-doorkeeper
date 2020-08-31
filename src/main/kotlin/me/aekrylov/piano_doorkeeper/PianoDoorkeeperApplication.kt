package me.aekrylov.piano_doorkeeper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.core.RedisTemplate

@SpringBootApplication
class PianoDoorkeeperApplication {

    @Bean
    fun storageService(redisTemplate: RedisTemplate<String, String>) = RedisStorageService(redisTemplate)
}

fun main(args: Array<String>) {
    runApplication<PianoDoorkeeperApplication>(*args)
}
