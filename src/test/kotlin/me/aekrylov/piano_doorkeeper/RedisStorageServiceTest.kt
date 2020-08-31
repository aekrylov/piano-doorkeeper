package me.aekrylov.piano_doorkeeper

import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class RedisStorageServiceTest {

    private lateinit var service: StorageService

    private val id = ID.next()
    private val user = User(id)
    private val room = id

    @BeforeEach
    fun setup() {
        service = RedisStorageService(
                StringRedisTemplate(LettuceConnectionFactory(RedisStandaloneConfiguration(
                        redis.host,
                        redis.firstMappedPort
                ))))
    }

    @Test
    fun `user should be able to enter a room successfully`() {
        service.enterRoom(user, room).shouldBeInstanceOf<Success>()
        service.getRoom(user) shouldBe room
    }

    @Test
    fun `user should be able leave the room they'd entered`() {
        service.enterRoom(user, room)

        service.leaveRoom(user, room) shouldBe true
        service.getRoom(user) shouldBe null
    }

    @Test
    fun `user shouldn't enter more than one room`() {
        service.enterRoom(user, room)

        service.enterRoom(user, room * 2).shouldBeInstanceOf<AlreadyEntered> {
            it.currentRoomId shouldBe room
        }
        service.getRoom(user) shouldBe room
    }

    @Test
    fun `user shouldn't be allowed to leave the room being in a different room`() {
        service.enterRoom(user, room)

        service.leaveRoom(user, room * 2) shouldBe false
    }

    @Test
    fun `user shouldn't be allowed to leave rooms before entering any`() {
        service.leaveRoom(user, room) shouldBe false
    }

    companion object {

        @Container
        var redis: RedisContainer = RedisContainer()
                .withExposedPorts(6379)

    }
}