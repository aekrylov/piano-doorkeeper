package me.aekrylov.piano_doorkeeper.web

import com.fasterxml.jackson.annotation.JsonInclude
import me.aekrylov.piano_doorkeeper.User
import me.aekrylov.piano_doorkeeper.service.AccessControlService
import me.aekrylov.piano_doorkeeper.service.AlreadyEntered
import me.aekrylov.piano_doorkeeper.service.StorageService
import me.aekrylov.piano_doorkeeper.service.Success
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Positive

@RestController
@RequestMapping("/")
class KeeperController(
        private val accessControlService: AccessControlService,
        private val storageService: StorageService
) {

    companion object : Logging

    @GetMapping("/check")
    fun check(@Valid request: CheckRequest): ResponseEntity<CheckResponse> {
        val user = User(request.keyId)

        if (!request.entrance) {
            //todo let them leave?
            if(!accessControlService.hasAccess(request.roomId, user)) {
                logger.warn { "User $user tries to leave room ${request.roomId} while not having access to it" }
                return ResponseEntity.status(403).body(AccessDenied)
            }

            if (!storageService.leaveRoom(user, request.roomId)) {
                return ResponseEntity.status(403).body(LeaveNotInRoom)
            }
            return ResponseEntity.ok(LeaveSuccess)
        }

        if(!accessControlService.hasAccess(request.roomId, user)) {
            return ResponseEntity.status(403).body(AccessDenied)
        }

        return when (val response = storageService.enterRoom(user, request.roomId)) {
            Success -> ResponseEntity.ok(EnterSuccess)
            is AlreadyEntered -> ResponseEntity.status(403).body(EnterDifferentRoom(response.currentRoomId))
        }

    }

}

data class CheckRequest(
        @field:javax.validation.constraints.NotNull
        @field:Positive
        val roomId: Int,

        @field:javax.validation.constraints.NotNull
        @field:Positive
        val keyId: Int,

        @field:javax.validation.constraints.NotNull
        val entrance: Boolean
)

@JsonInclude(JsonInclude.Include.NON_NULL)
sealed class CheckResponse(code: String, val message: String? = null) : RestResponse(code)

object AccessDenied: CheckResponse("access_denied")

object EnterSuccess: CheckResponse("enter_success")

class EnterDifferentRoom(val roomId: Int): CheckResponse("enter_different_room")

object LeaveSuccess: CheckResponse("leave_success")

object LeaveNotInRoom: CheckResponse("leave_not_in_room")
