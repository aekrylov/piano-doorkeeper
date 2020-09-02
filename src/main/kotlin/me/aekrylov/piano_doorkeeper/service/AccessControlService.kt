package me.aekrylov.piano_doorkeeper.service

import me.aekrylov.piano_doorkeeper.User
import org.springframework.stereotype.Service

@Service
class AccessControlService {

    fun hasAccess(roomId: Int, user: User): Boolean = user.id % roomId == 0
}