/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/27 18:23:37
 *
 * KLib/KLib.main/PluginAccessor.kt
 */

@file:Suppress("ObjectPropertyName")

package io.github.karlatemp.klib.reflect

import io.github.karlatemp.klib.unsafe.Reflection
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodType
import java.util.function.Function

@Suppress("UNCHECKED_CAST")
private val `Plugin-getFile`: Function<JavaPlugin, File> by lazy {
    val lookup = Reflection.getRoot().`in`(JavaPlugin::class.java)
    val handle = lookup.findVirtual(JavaPlugin::class.java, "getFile", MethodType.methodType(File::class.java))
    LambdaMetafactory.metafactory(
        lookup, "apply",
        MethodType.methodType(Function::class.java),
        MethodType.methodType(Object::class.java, Object::class.java),
        handle,
        MethodType.methodType(File::class.java, JavaPlugin::class.java)
    ).target.invoke() as Function<JavaPlugin, File>
}

val JavaPlugin.jarFile: File get() = `Plugin-getFile`.apply(this)
