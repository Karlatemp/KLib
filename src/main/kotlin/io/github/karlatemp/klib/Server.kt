/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/15 13:11:25
 *
 * KLib/KLib.main/Server.kt
 */

@file:JvmName("Utils")
@file:JvmMultifileClass

package io.github.karlatemp.klib

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.plugin.SimplePluginManager
import org.bukkit.plugin.java.JavaPlugin

val commandMap: CommandMap by lazy {
    val server = Bukkit.getServer()
    // Try get from server
    runCatching {
        return@lazy server.javaClass.getDeclaredMethod("getCommandMap").also {
            it.isAccessible = true
        }.invoke(server) as CommandMap
    }
    // Try get from PluginManager
    val manager = server.pluginManager as SimplePluginManager
    runCatching {
        return@lazy SimplePluginManager::class.java.getDeclaredField("commandMap").also { it.isAccessible = true }
            .get(manager) as CommandMap
    }
    runCatching {
        val field = Command::class.java.getDeclaredField("commandMap").also { it.isAccessible = true }
        val plugins = server.pluginManager.plugins
        runCatching {
            repeat(5) {
                val cmd = ((plugins.random() as? JavaPlugin) ?: return@repeat).let {
                    val commands = it.description.commands
                    if (commands.isEmpty()) return@repeat
                    it.getCommand(commands.keys.random())!!
                }
                return@lazy field[cmd] as CommandMap
            }
        }
    }
    error("No Field get")
}

val obcPath: String by lazy {
    val server = Bukkit.getServer()
    // org.bukkit.craftbukkit.****.CraftServer
    val klass = server.javaClass.name
    val splitter = klass.lastIndexOf('.')
    return@lazy klass.substring(0, splitter)
}
val nmsVersion: String by lazy {
    val op = obcPath
    val splitter = op.lastIndexOf('.')
    return@lazy op.substring(splitter + 1)
}
val nmsPath: String by lazy { "net.minecraft.server.$nmsVersion" }
