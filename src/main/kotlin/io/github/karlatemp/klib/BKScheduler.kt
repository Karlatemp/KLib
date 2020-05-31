/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/12 21:36:52
 *
 * KLib/KLib.main/BKScheduler.kt
 */

package io.github.karlatemp.klib

import kotlinx.coroutines.CoroutineDispatcher
import org.bukkit.Bukkit
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun scheduler(): BukkitScheduler {
    @OptIn(ExperimentalStdlibApi::class)
    return coroutineContext[CoroutineDispatcher] as? BukkitScheduler ?: error("No BukkitScheduler found.")
}

suspend fun ensureServerThread() {
    if (!Bukkit.isPrimaryThread()) {
        nextTick()
    }
}

suspend fun nextTick() {
    val scheduler = scheduler()
    return suspendCoroutine {
        BukkitScheduler.scheduler.runTask(scheduler.owner, Runnable {
            Thread.dumpStack()
            it.resume(Unit)
        })
    }
}

suspend fun nextAsyncTick() {
    val scheduler = scheduler()
    return suspendCoroutine {
        BukkitScheduler.scheduler.runTaskAsynchronously(scheduler.owner, Runnable {
            scheduler.directRun.set(true)
            it.resume(Unit)
        })
    }
}
