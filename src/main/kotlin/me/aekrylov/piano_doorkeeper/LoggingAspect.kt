package me.aekrylov.piano_doorkeeper

import me.aekrylov.piano_doorkeeper.service.AlreadyEntered
import me.aekrylov.piano_doorkeeper.service.EnterRoomResponse
import me.aekrylov.piano_doorkeeper.service.Success
import org.apache.logging.log4j.kotlin.Logging
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {

    companion object : Logging

    @AfterReturning("execution(* me.aekrylov.piano_doorkeeper.service.StorageService.enterRoom(..)) && args(user, roomId)",
            returning = "response")
    fun profileRoomEnter(user: User, roomId: Int, response: EnterRoomResponse) {
        when (response) {
            Success -> logger.debug { "User $user has entered room $roomId" }
            is AlreadyEntered -> logger.warn {
                "User $user tried to enter room $roomId while being inside room ${response.currentRoomId}"
            }
        }
    }

    @AfterReturning("execution(* me.aekrylov.piano_doorkeeper.service.StorageService.leaveRoom(..)) && args(user, roomId)",
            returning = "success")
    fun profileRoomLeave(user: User, roomId: Int, success: Boolean) {
        if (success) {
            logger.debug { "User $user has left room $roomId" }
        } else {
            logger.warn { "User $user tried to leave room $roomId while not being in this room" }
        }
    }
}