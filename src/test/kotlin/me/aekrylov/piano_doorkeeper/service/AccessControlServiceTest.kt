package me.aekrylov.piano_doorkeeper.service

import io.kotlintest.shouldBe
import me.aekrylov.piano_doorkeeper.User
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class AccessControlServiceTest {

    private val service = AccessControlService()

    @ParameterizedTest
    @MethodSource("positives")
    fun `positive cases`(roomId: Int, user: User) {
        service.hasAccess(roomId, user) shouldBe true
    }

    @ParameterizedTest
    @MethodSource("negatives")
    fun `negative cases`(roomId: Int, user: User) {
        service.hasAccess(roomId, user) shouldBe false
    }

    companion object {

        @JvmStatic
        fun positives() = listOf(
                Arguments.of(1, User(1)),
                Arguments.of(1, User(3)),
                Arguments.of(14, User(42))
        )

        @JvmStatic
        fun negatives() = listOf(
                Arguments.of(2, User(1)),
                Arguments.of(2, User(3)),
                Arguments.of(14, User(15))
        )
    }
}