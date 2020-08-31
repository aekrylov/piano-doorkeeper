package me.aekrylov.piano_doorkeeper

import org.testcontainers.containers.GenericContainer

class RedisContainer : GenericContainer<RedisContainer>("redis:6.0-alpine")