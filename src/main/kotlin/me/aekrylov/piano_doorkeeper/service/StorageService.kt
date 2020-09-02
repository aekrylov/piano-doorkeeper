package me.aekrylov.piano_doorkeeper.service

import me.aekrylov.piano_doorkeeper.User
import java.util.concurrent.ConcurrentHashMap

interface StorageService {

    /**
     * Called when user enters the room. If the user has entered any room already, nothing happens
     *
     */
    fun enterRoom(user: User, roomId: Int): EnterRoomResponse

    /**
     * Called when user leaves the room. If the user is not in this room then nothing happens
     *
     * @return false if the user is not in this room; true otherwise
     */
    fun leaveRoom(user: User, roomId: Int): Boolean

    fun getRoom(user: User): Int?
}

sealed class EnterRoomResponse

object Success: EnterRoomResponse()

data class AlreadyEntered(val currentRoomId: Int): EnterRoomResponse()

open class SimpleStorageService: StorageService {

    private val state: ConcurrentHashMap<User, Int> = ConcurrentHashMap()

    override fun enterRoom(user: User, roomId: Int): EnterRoomResponse =
            when(val currentRoomId = state.putIfAbsent(user, roomId)) {
                null -> Success
                else -> AlreadyEntered(currentRoomId)
            }

    override fun leaveRoom(user: User, roomId: Int): Boolean = state.remove(user, roomId)

    override fun getRoom(user: User): Int? = state[user]
}
