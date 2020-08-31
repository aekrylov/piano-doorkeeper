package me.aekrylov.piano_doorkeeper

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class KeeperController(
        private val accessControlService: AccessControlService,
        private val storageService: StorageService
) {

    @GetMapping("/check")
    fun check(request: CheckRequest): ResponseEntity<CheckResponse> {
        val user = User(request.keyId)

        if (!request.entrance) {
            //todo let them leave?
            if(!accessControlService.hasAccess(request.roomId, user)) {
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
        val roomId: Int,
        val keyId: Int,
        val entrance: Boolean
)

@JsonInclude(JsonInclude.Include.NON_NULL)
sealed class CheckResponse(val code: String, val message: String? = null)

object AccessDenied: CheckResponse("access_denied")

object EnterSuccess: CheckResponse("enter_success")

class EnterDifferentRoom(val roomId: Int): CheckResponse("enter_different_room")

object LeaveSuccess: CheckResponse("leave_success")

object LeaveNotInRoom: CheckResponse("leave_not_in_room")
