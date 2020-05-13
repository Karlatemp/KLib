/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/13 22:44:49
 *
 * KLib/KLib.main/I18n.kt
 */

package io.github.karlatemp.klib

import io.github.karlatemp.klib.formatter.FEFormatter
import io.github.karlatemp.klib.formatter.NoneTemplate
import io.github.karlatemp.klib.formatter.Template
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Function

class I18n(
    private val wordKey: (String) -> String?
) {
    private val messages =
        ConcurrentHashMap<String, Template>()

    fun clear() {
        messages.clear()
    }

    fun reset(key: String) {
        messages.remove(key)
    }

    private val merger: Function<String, Template> = Function { key ->
        return@Function wordKey(key)?.let {
            FEFormatter.parse(it)
        } ?: NoneTemplate(key)
    }

    operator fun get(key: String): Template {
        return messages.computeIfAbsent(key, merger)
    }

}