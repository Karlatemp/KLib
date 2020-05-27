/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/13 18:01:52
 *
 * KLib/KLib.main/Plugins.kt
 */

package io.github.karlatemp.klib

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private object PluginsPlaceholder

private val javaClass = Class.forName(PluginsPlaceholder::class.java.`package`.name + ".PluginsKt")

private object TrackedSecurityManager : SecurityManager() {
    public override fun getClassContext(): Array<Class<*>> {
        return super.getClassContext()
    }
}

val context: Array<Class<*>>
    get() {
        val context = TrackedSecurityManager.classContext
        context.forEachIndexed { index, ct ->
            @Suppress("UNCHECKED_CAST")
            if (ct === javaClass) {
                val array = arrayOfNulls<Class<*>>(context.size - index - 1)
                System.arraycopy(context, index + 1, array, 0, array.size)
                return array as Array<Class<*>>
            }
        }
        error("Internal error: No splitter")
    }

val caller: Class<*>
    get() {
        return context[2]
    }

inline fun <reified T : JavaPlugin> pluginOf(): T {
    return JavaPlugin.getPlugin(T::class.java)
}

inline fun <reified T> plugin(): JavaPlugin {
    return JavaPlugin.getProvidingPlugin(T::class.java)
}

@Suppress("NOTHING_TO_INLINE")
inline fun Class<*>.plugin(): JavaPlugin = JavaPlugin.getProvidingPlugin(this)

fun dump() {
    Exception("Stack trace").printStackTrace(System.out)
}
