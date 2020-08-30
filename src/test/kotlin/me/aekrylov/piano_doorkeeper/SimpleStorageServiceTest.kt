package me.aekrylov.piano_doorkeeper

import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SimpleStorageServiceTest {

    private lateinit var service: SimpleStorageService
    private val user = User(1)

    @BeforeEach
    fun setup() {
        service = SimpleStorageService()
    }

    @Test
    fun `user should be able to enter a room successfully`() {
        service.enterRoom(user, 1).shouldBeInstanceOf<Success>()
        service.getRoom(user) shouldBe 1
    }

    @Test
    fun `user should be able leave the room they'd entered`() {
        service.enterRoom(user, 1)

        service.leaveRoom(user, 1) shouldBe true
        service.getRoom(user) shouldBe null
    }

    @Test
    fun `user shouldn't enter more than one room`() {
        service.enterRoom(user, 1)

        service.enterRoom(user, 2).shouldBeInstanceOf<AlreadyEntered> {
            it.currentRoomId shouldBe 1
        }
        service.getRoom(user) shouldBe 1
    }

    @Test
    fun `user shouldn't be allowed to leave the room being in a different room`() {
        service.enterRoom(user, 1)

        service.leaveRoom(user, 2) shouldBe false
    }

    @Test
    fun `user shouldn't be allowed to leave rooms before entering any`() {
        service.leaveRoom(user, 1) shouldBe false
    }
}