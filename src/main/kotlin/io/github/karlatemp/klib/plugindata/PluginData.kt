/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/27 17:22:01
 *
 * KLib/KLib.main/PluginData.kt
 */

package io.github.karlatemp.klib.plugindata

import io.github.karlatemp.klib.pluginOf
import io.github.karlatemp.klib.unsafe.Reflection
import io.github.karlatemp.klib.unsafe.Unsafe
import org.bukkit.plugin.java.JavaPlugin
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.util.concurrent.ConcurrentHashMap

private val unsafe = Unsafe.getUnsafe()

internal fun dataClassOf(plugin: JavaPlugin): Class<*> {
    val klass = plugin.javaClass
    val klassLoader = klass.classLoader
    return try {
        Class.forName("${klass.name}-KLib-Declared", false, klassLoader)
    } catch (any: Throwable) {
        defineDeclare(klass, klassLoader)
    }
}

internal fun defineDeclare(klass: Class<JavaPlugin>, loader: ClassLoader?): Class<*> {
    return Reflection.defineClass(loader, ClassWriter(0).also {
        with(it) {
            visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC,
                klass.name.replace('.', '/') + "-KLib-Declared",
                null,
                "java/lang/Object",
                null
            )
            visitField(Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, "metadata", "Ljava/util/Map;", null, null)
        }
    }, null).also {
        unsafe.ensureClassInitialized(it)
        val field = it.getField("metadata")
        unsafe.putReference(
            unsafe.staticFieldBase(field),
            unsafe.staticFieldOffset(field),
            ConcurrentHashMap<Any, Any>()
        )
    }
}

@Suppress("UNCHECKED_CAST")
internal fun Class<*>.getDeclared(): MutableMap<String, Any> =
    getField("metadata").get(null) as MutableMap<String, Any>

fun metadataOf(plugin: JavaPlugin) = dataClassOf(plugin).getDeclared()

inline fun <reified T : JavaPlugin> metadataOf() = metadataOf(pluginOf<T>())
