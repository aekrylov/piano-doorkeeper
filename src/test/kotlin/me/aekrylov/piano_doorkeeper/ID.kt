package me.aekrylov.piano_doorkeeper

import java.util.concurrent.atomic.AtomicInteger

object ID {

    private val counter: AtomicInteger = AtomicInteger(10)

    fun next() = counter.getAndIncrement()
}