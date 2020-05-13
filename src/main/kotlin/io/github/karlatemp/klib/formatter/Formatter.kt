/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/13 21:26:55
 *
 * KLib/KLib.main/Formatter.kt
 */

@file:Suppress("MemberVisibilityCanBePrivate")

package io.github.karlatemp.klib.formatter

import java.util.*

abstract class Formatter {
    abstract fun parse(
        template: String
    ): Template
}

class NoneTemplate(private val key: String) : Template() {
    override fun format(buffer: StringBuilder, vararg arguments: String) {
        buffer.append("{unknown i18n $key}")
    }
}

abstract class Template {
    fun doFormat(
        buffer: StringBuilder,
        vararg arguments: String
    ): StringBuilder {
        return buffer.also { format(it, *arguments) }
    }

    operator fun get(vararg arguments: String): String {
        return StringBuilder().also { format(it, *arguments) }.toString()
    }

    abstract fun format(
        buffer: StringBuilder,
        vararg arguments: String
    )
}

abstract class Action {
    abstract fun apply(
        buffer: StringBuilder,
        vararg arguments: String
    )
}

open class LinkedTemplate(
    val actions: Collection<Action>
) : Template() {
    override fun toString(): String {
        return "LinkedTemplate{$actions}"
    }

    override fun format(buffer: StringBuilder, vararg arguments: String) {
        actions.forEach {
            it.apply(buffer, *arguments)
        }
    }
}

open class ArgumentAction(val slot: Int) : Action() {
    override fun toString(): String {
        return "Argument{$slot}"
    }

    override fun apply(
        buffer: StringBuilder,
        vararg arguments: String
    ) {
        (buffer.getOrNull(slot)
            ?: return buffer.append("{$slot}").let {})
            .apply { buffer.append(this) }
    }
}

open class TextAction(
    val content: String,
    val start: Int = 0,
    end: Int = -1
) : Action() {
    override fun toString(): String {
        return "Text{${content.substring(start, end)}}"
    }

    val end = if (end == -1) content.length else end
    override fun apply(buffer: StringBuilder, vararg arguments: String) {
        buffer.append(content, start, end)
    }
}

object FEFormatter : Formatter() {
    override fun parse(template: String): Template {
        val linked = LinkedList<Action>()
        var index = 0
        while (true) {
            val ind = template.indexOf('{', index)
            if (ind == -1) break

            linked.add(TextAction(template, index, ind))
            index = ind

            val ed = template.indexOf('}', ind)
            if (ed == -1) break

            index = ed + 1
            val ct = template.substring(ind + 1, ed)

            ct.toIntOrNull()?.also {
                linked.add(ArgumentAction(it))
            } ?: kotlin.run {
                linked.add(TextAction(template, ind, index))
            }
        }
        linked.add(TextAction(template, index))
        return LinkedTemplate(linked)
    }

}
