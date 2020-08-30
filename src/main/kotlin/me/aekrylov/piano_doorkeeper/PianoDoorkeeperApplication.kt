package me.aekrylov.piano_doorkeeper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class PianoDoorkeeperApplication {

    @Bean
    fun storageService() = SimpleStorageService()
}

fun main(args: Array<String>) {
    runApplication<PianoDoorkeeperApplication>(*args)
}
