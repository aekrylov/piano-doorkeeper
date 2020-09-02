package me.aekrylov.piano_doorkeeper.service

import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import me.aekrylov.piano_doorkeeper.ID
import me.aekrylov.piano_doorkeeper.RedisContainer
import me.aekrylov.piano_doorkeeper.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.TestPropertySourceUtils
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = [RedisStorageServiceTest.PropertyInitializer::class])
class RedisStorageServiceTest {

    @Autowired
    private lateinit var service: StorageService

    private val id = ID.next()
    private val user = User(id)
    private val room = id

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

    class PropertyInitializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.redis.host=${redis.host}",
                    "spring.redis.port=${redis.firstMappedPort}"
            )
        }
    }

}