/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/12 18:07:12
 *
 * KLib/KLib.main/BukkitStartup.kt
 */

package io.github.karlatemp.klib

import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin

class BukkitStartup : JavaPlugin() {
    companion object {
        private var instance0: BukkitStartup? = null
        val instance get() = instance0!!
    }

    init {
        instance0 = this
    }

    override fun onEnable() {
        listen<AsyncPlayerChatEvent> {
            if (message == "复读") {
                listen<AsyncPlayerChatEvent> { job ->
                    if (message == "取消") {
                        job.complete()
                    } else {
                        nextAsyncTick()
                        player.sendMessage(message)
                    }
                }
            }
        }
    }
}