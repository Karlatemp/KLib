/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/12 18:07:12
 *
 * KLib/KLib.main/BukkitStartup.kt
 */

package io.github.karlatemp.klib

import io.github.karlatemp.klib.i18n.i18n
import io.github.karlatemp.klib.i18n.i18nLoader
import io.github.karlatemp.klib.plugindata.dataClassOf
import io.github.karlatemp.klib.plugindata.metadataOf
import io.github.karlatemp.klib.reflect.jarFile
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
        if (java.lang.Boolean.getBoolean("klib.test")) {
            openTest()
        }
    }

    private fun openTest() {
        println(obcPath)
        println(nmsPath)
        println(nmsVersion)
        println(metadataOf(this) === metadataOf(this))
        println(dataClassOf(this))
        println(this.jarFile)
        println("Loader: $i18nLoader")
        println(this.i18n)
        println(this.i18n["test"].get())
    }
}
