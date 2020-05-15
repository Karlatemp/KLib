/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/12 18:07:12
 *
 * KLib/KLib.main/BukkitStartup.kt
 */

package io.github.karlatemp.klib

import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.player.PlayerJoinEvent
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

    fun openTest() {
        println(obcPath)
        println(nmsPath)
        println(nmsVersion)
        listen<PlayerJoinEvent> {
            player.inventory.addItem(buildItem {
                this type Material.DIAMOND named "加速火把" lore {
                    add("该加速了")
                } attribute {
                    this.attribute(Attribute.GENERIC_MOVEMENT_SPEED)
                        .amount(3.0)
                        .operation(AttributeModifier.Operation.ADD_NUMBER)
                }
                Enchantment.DAMAGE_ALL enchant 444
            })
        }
    }
}