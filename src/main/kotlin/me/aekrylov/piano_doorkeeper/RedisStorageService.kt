package me.aekrylov.piano_doorkeeper

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript

/**
 * Redis-backed storage service
 * TODO can only use strings for some reason
 * TODO maybe use something other than Lua scripts for ACID
 */
open class RedisStorageService(private val template: RedisTemplate<String, String>) : StorageService {

    private val hash = template.boundHashOps<String, String>("rooms")

    // used to get another room atomically
    private val putIfAbsentScript = DefaultRedisScript<String>("""
        if(redis.call('hsetnx', KEYS[1], ARGV[1], ARGV[2]) > 0) then 
            return nil
        else 
            return redis.call('hget', KEYS[1], ARGV[1])
        end
    """.trimIndent(), String::class.java)

    private val deleteScript = DefaultRedisScript<String>("""
        local existing = redis.call('hget', KEYS[1], ARGV[1]) 
        if(existing == ARGV[2]) then
            redis.call('hdel', KEYS[1], ARGV[1])
            return "true"
        else
            return "false"
        end
    """.trimIndent(), String::class.java)

    override fun enterRoom(user: User, roomId: Int): EnterRoomResponse =
            when (val anotherRoom: String? = template.execute(putIfAbsentScript, listOf("rooms"), user.id.toString(), roomId.toString())) {
                null -> {
                    Success
                }
                else -> {
                    AlreadyEntered(anotherRoom.toInt())
                }
            }


    override fun leaveRoom(user: User, roomId: Int): Boolean =
            template.execute(deleteScript, listOf("rooms"), user.id.toString(), roomId.toString()).toBoolean()

    override fun getRoom(user: User): Int? = hash[user.id.toString()]?.toInt()
}