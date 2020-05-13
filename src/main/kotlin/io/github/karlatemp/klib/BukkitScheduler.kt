/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/12 18:07:12
 *
 * KLib/KLib.main/BukkitScheduler.kt
 */

package io.github.karlatemp.klib

import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

@OptIn(InternalCoroutinesApi::class)
class BukkitScheduler(val owner: Plugin) : ExecutorCoroutineDispatcher(), Delay, Executor {
    override val executor: Executor
        get() = this
    @ExperimentalStdlibApi
    override val key: CoroutineContext.Key<*>
        get() = CoroutineDispatcher

    companion object {
        val scheduler = Bukkit.getScheduler();
    }

    override fun close() {
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        execute(block)
    }

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        if (Bukkit.isPrimaryThread()) {
            scheduler.runTaskLater(owner, Runnable {
                continuation.resume(Unit)
            }, timeMillis / 50)
        } else {
            scheduler.runTaskLaterAsynchronously(owner, Runnable {
                continuation.resume(Unit)
            }, timeMillis / 50)
        }
    }

    override fun execute(command: Runnable) {
        if (Bukkit.isPrimaryThread()) {
            command.run()
        } else {
            scheduler.runTask(owner, command)
        }
    }
}
