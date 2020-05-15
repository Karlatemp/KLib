/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/15 19:01:43
 *
 * KLib/KLib.main/ItemStack.kt
 */
@file:JvmName("Utils")
@file:JvmMultifileClass

package io.github.karlatemp.klib

import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

fun ItemStack.editMeta(invoke: ItemStack.(ItemMeta) -> Unit) {
    itemMeta = (itemMeta ?: error("No itemMeta from AIR")).also {
        invoke(this, it)
    }
}

var ItemStack.displayName: String?
    get() {
        return (itemMeta ?: return null).also {
            if (!it.hasDisplayName()) return null
        }.displayName
    }
    set(value) {
        editMeta { it.setDisplayName(value) }
    }

var ItemStack.lore: List<String>?
    get() {
        val a = (itemMeta ?: return null).lore
        return a
    }
    set(value) {
        editMeta { it.lore = value }
    }

fun ItemStack.editLore(invoke: ItemStack.(meta: ItemMeta, lore: MutableList<String>) -> Unit) {
    editMeta { meta ->
        val lore = LinkedList<String>()
        meta.lore?.apply { lore.addAll(this) }
        invoke(this, meta, lore)
        meta.lore = lore.takeUnless { it.isEmpty() }
    }
}

@DslMarker
annotation class ItemStackBuilderDsl

@DslMarker
annotation class AttributeModifierBuilderDsl

@AttributeModifierBuilderDsl
class AttributeModifierBuilder {

    private var uuid: UUID? = null
    private var name: String? = null
    private var amount = 0.0
    private var operation: AttributeModifier.Operation? = null
    private var slot: EquipmentSlot? = null
    public var attribute: Attribute? = null

    @AttributeModifierBuilderDsl
    infix fun uuid(uuid: UUID): AttributeModifierBuilder {
        this.uuid = uuid; return this
    }

    @AttributeModifierBuilderDsl
    infix fun attribute(attribute: Attribute): AttributeModifierBuilder {
        this.attribute = attribute; return this
    }

    @AttributeModifierBuilderDsl
    infix fun name(name: String): AttributeModifierBuilder {
        this.name = name; return this
    }

    @AttributeModifierBuilderDsl
    infix fun amount(amount: Double): AttributeModifierBuilder {
        this.amount = amount; return this
    }

    @AttributeModifierBuilderDsl
    infix fun operation(operation: AttributeModifier.Operation): AttributeModifierBuilder {
        this.operation = operation; return this
    }

    infix fun slot(slot: EquipmentSlot): AttributeModifierBuilder {
        this.slot = slot; return this
    }

    fun build(): AttributeModifier {
        return AttributeModifier(
            uuid ?: UUID.randomUUID(),
            name ?: UUID.randomUUID().toString(),
            amount,
            operation ?: error("`operation` not set"),
            slot
        )
    }
}

@ItemStackBuilderDsl
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ItemStackBuilder(val item: ItemStack) {
    private var _meta = item.itemMeta
    val meta: ItemMeta get() = _meta ?: error("No itemMeta")

    @ItemStackBuilderDsl
    infix fun type(material: Material): ItemStackBuilder {
        finish()
        item.type = material
        refresh()
        return this
    }

    @ItemStackBuilderDsl
    infix fun count(count: Int): ItemStackBuilder {
        item.amount = count
        return this
    }

    @ItemStackBuilderDsl
    infix fun damage(damage: Int): ItemStackBuilder {
        (meta as? Damageable)?.apply {
            this.damage = damage
        }
        return this
    }

    @ItemStackBuilderDsl
    infix fun display(display: String): ItemStackBuilder {
        meta.setDisplayName(display)
        return this
    }

    @ItemStackBuilderDsl
    infix fun lore(lore: MutableList<String>.() -> Unit): ItemStackBuilder {
        val lore0 = LinkedList<String>()
        meta.lore?.apply { lore0.addAll(this) }
        lore(lore0)
        meta.lore = lore0.takeUnless { it.isEmpty() }
        return this
    }

    @ItemStackBuilderDsl
    infix fun Enchantment.enchant(level: Int) {
        meta.addEnchant(this, level, true)
    }

    fun finish(): ItemStackBuilder {
        item.itemMeta = _meta
        return this
    }

    fun refresh(): ItemStackBuilder {
        _meta = item.itemMeta
        return this
    }

    @Suppress("NOTHING_TO_INLINE")
    @ItemStackBuilderDsl
    inline infix fun named(display: String): ItemStackBuilder {
        return this display display
    }

    @ItemStackBuilderDsl
    infix fun attribute(invoke: AttributeModifierBuilder.() -> Unit): ItemStackBuilder {
        return AttributeModifierBuilder().also(invoke).let {
            meta.addAttributeModifier(
                it.attribute ?: error("Attribute not set"),
                it.build()
            )
            this
        }
    }
}

fun buildItem(
    count: Int = 1,
    type: Material = Material.AIR,
    invoke: ItemStackBuilder.() -> Unit
) = ItemStackBuilder(ItemStack(type, count)).also {
    invoke(it)
}.finish().item