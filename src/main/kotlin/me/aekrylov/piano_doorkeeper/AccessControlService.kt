package me.aekrylov.piano_doorkeeper

import org.springframework.stereotype.Service

@Service
class AccessControlService {

    fun hasAccess(roomId: Int, user: User): Boolean = user.id % roomId == 0
}