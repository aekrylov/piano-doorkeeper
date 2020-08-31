package me.aekrylov.piano_doorkeeper

import org.springframework.data.redis.core.RedisTemplate

class RedisStorageService(template: RedisTemplate<String, String>) : StorageService {

    //todo cant use int for some reason
    private val hash = template.boundHashOps<String, String>("rooms")

    override fun enterRoom(user: User, roomId: Int): EnterRoomResponse =
            if (hash.putIfAbsent(user.id.toString(), roomId.toString()) != false) {
                Success
            } else {
                //todo atomicity
                AlreadyEntered(hash[user.id.toString()]!!.toInt())
            }


    override fun leaveRoom(user: User, roomId: Int): Boolean = hash.delete(user.id.toString())!! > 0

    override fun getRoom(user: User): Int? = hash[user.id]?.toInt()
}