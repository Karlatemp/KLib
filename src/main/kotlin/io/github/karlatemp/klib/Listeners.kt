/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/12 21:45:52
 *
 * KLib/KLib.main/Listeners.kt
 */

@file:Suppress("NOTHING_TO_INLINE")

package io.github.karlatemp.klib

import kotlinx.coroutines.*
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.RegisteredListener
import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType

fun <T : Event> Class<T>.handlers() = getMethod("getHandlerList").invoke(null) as HandlerList

@PublishedApi
internal fun <T : Event> Plugin.listen0(
    klass: Class<out Event>,
    priority: EventPriority,
    ignoreCancelled: Boolean,
    caller: MethodHandles.Lookup,
    handler: suspend T.(CompletableJob) -> Unit
): CompletableJob {
    val scheduler = BukkitScheduler(this)
    val scope = CoroutineScope(scheduler)
    val handlers = klass.handlers()
    val job = SupervisorJob(null)
    val listener = RegisteredListener(
        caller.newListener(), EventExecutor { _, event ->
            if (klass.isInstance(event)) {
                scope.launch {
                    @Suppress("UNCHECKED_CAST")
                    handler.invoke(event as T, job)
                }
            }
        },
        priority, this, ignoreCancelled
    )
    handlers.register(listener)
    return job.also {
        it.invokeOnCompletion {
            handlers.unregister(listener)
        }
    }
}

private val type0 = MethodType.methodType(Integer.TYPE, Object::class.java)
private val type1 = MethodType.methodType(Listener::class.java)

private val handle: MethodHandles.Lookup.() -> MethodHandle = run {
    var handle: MethodHandle? = null
    return@run resp@{
        return@resp handle ?: kotlin.run {
            //int Object.hashCode()
            return@resp this.findVirtual(
                Object::class.java,
                "hashCode",
                MethodType.methodType(Integer.TYPE)
            ).also {
                handle = it
            }
        }
    }
}

fun MethodHandles.Lookup.newListener(): Listener {
    val handle = handle(this)
    return LambdaMetafactory.metafactory(
        this, "a",
        type1, type0, handle, type0
    ).target.invoke() as Listener
}

inline fun <reified T : Event> Plugin.listen(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline handler: suspend T.(CompletableJob) -> Unit
): CompletableJob {
    val klass = T::class.java
    return listen0(klass, priority, ignoreCancelled, lookup(), handler)
}

@PublishedApi
internal suspend fun <T : Event> Plugin.nextEvent0(
    klass: Class<T>,
    priority: EventPriority,
    ignoreCancelled: Boolean,
    callback: suspend CompletableJob.() -> Unit,
    caller: MethodHandles.Lookup,
    filter: T.() -> Boolean
): T {
    val cp = CompletableDeferred<T>()
    callback(listen0<T>(klass, priority, ignoreCancelled, caller) {
        if (filter(this)) {
            it.complete()
            cp.complete(this)
        }
    })
    return cp.await()
}

suspend inline fun <reified T : Event> Plugin.nextEvent(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline callback: suspend CompletableJob.() -> Unit = {},
    noinline filter: T.() -> Boolean
): T = nextEvent0(T::class.java, priority, ignoreCancelled, callback, lookup(), filter)

suspend fun CompletableJob.cancelWhenPlayerLeft(player: Player) {
    scheduler().owner.listen<PlayerQuitEvent> {
        this@cancelWhenPlayerLeft.complete()
    }.also { job ->
        invokeOnCompletion {
            job.complete()
        }
    }
}

inline fun lookup(): MethodHandles.Lookup = MethodHandles.lookup()
