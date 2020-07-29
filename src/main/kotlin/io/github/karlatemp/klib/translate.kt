/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/07/25 21:07:09
 *
 * KLib/KLib.main/translate.kt
 */

package io.github.karlatemp.klib

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.md_5.bungee.api.chat.*
import java.util.*
import java.util.function.Function
import java.util.regex.Pattern

typealias Translator = Function<String, String?>

@Suppress("NOTHING_TO_INLINE")
private inline operator fun Translator.get(key: String): String? = this.apply(key)

object StandardTranslator : Translator {
    val obj: JsonObject?

    init {
        val resource = StandardTranslator::class.java.getResourceAsStream("/i18n.minecraft-1.16")
        obj = resource?.reader(Charsets.UTF_8)?.use { JsonParser().parse(it) }?.asJsonObject
    }

    override fun apply(t: String): String? {
        return obj?.get(t)?.asString
    }
}

fun translate(
        components: Array<BaseComponent>,
        translator: Translator
) {
    components.forEachIndexed { index, component ->
        components[index] = translate(component, translator)
    }
}

private val format = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)")
private fun translate(
        component: BaseComponent,
        translator: Translator
): BaseComponent =
        when (component) {
            is TextComponent -> {
                val extra = component.extra
                extra?.forEachIndexed { index, baseComponent ->
                    extra[index] = translate(baseComponent, translator)
                }
                component
            }
            is TranslatableComponent -> {
                val trans = translator[component.translate]
                if (trans == null) component
                else {
                    val components = LinkedList<BaseComponent>()
                    val matcher = format.matcher(trans)
                    fun newTextComponent(text: String): TextComponent {
                        return TextComponent(text)
                    }

                    fun appendLastComponent(): TextComponent {
                        val last = components.last
                        val k = newTextComponent("")
                        return if (last == null) {
                            k
                        } else {
                            if (last is TextComponent) {
                                run {
                                    val w: TextComponent = last
                                    if (w.colorRaw == component.colorRaw &&
                                            w.isBoldRaw == component.isBoldRaw &&
                                            w.isItalicRaw == component.isItalicRaw &&
                                            w.isObfuscatedRaw == component.isObfuscatedRaw &&
                                            w.isStrikethroughRaw == component.isStrikethroughRaw &&
                                            w.isUnderlinedRaw == component.isUnderlinedRaw
                                    ) {
                                        w
                                    } else k
                                }
                            } else k
                        }.also { if (it === k) components.add(k) }
                    }

                    var position = 0
                    var i = 0
                    while (matcher.find(position)) {
                        val pos = matcher.start()
                        if (pos != position) {
                            components.add(newTextComponent(trans.substring(position, pos)))
                        }
                        position = matcher.end()

                        val formatCode = matcher.group(2)
                        when (formatCode[0]) {
                            '%' -> {
                                appendLastComponent().text += "%"
                            }
                            's', 'd' -> {
                                val withIndex = matcher.group(1)
                                val withComponent: BaseComponent =
                                        component.with[if (withIndex != null) withIndex.toInt() - 1 else i++]
                                components.add(translate(withComponent, translator))
                            }
                        }
                    }
                    if (trans.length != position) {
                        components.add(newTextComponent(trans.substring(position, trans.length)))
                    }
                    TextComponent().apply { components.forEach { addExtra(it) } }.apply {
                        component.colorRaw?.let { color = it }
                        component.isUnderlinedRaw?.let { isUnderlined = it }
                        component.isStrikethroughRaw?.let { isStrikethrough = it }
                        component.isObfuscatedRaw?.let { isObfuscated = it }
                        component.isBoldRaw?.let { isBold = it }
                        component.isItalicRaw?.let { isItalic = it }
                    }
                }
            }

            is KeybindComponent,
            is SelectorComponent,
            is ScoreComponent -> component
            else -> {
                throw AssertionError()
            }
        }.apply {
            hoverEvent?.let { he ->
                translate(he.value, translator)
            }
        }
