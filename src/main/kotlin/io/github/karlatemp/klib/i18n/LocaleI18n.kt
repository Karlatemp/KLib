/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/27 18:13:43
 *
 * KLib/KLib.main/LocaleI18n.kt
 */

package io.github.karlatemp.klib.i18n

import io.github.karlatemp.klib.formatter.FEFormatter
import io.github.karlatemp.klib.formatter.Formatter
import io.github.karlatemp.klib.formatter.NoneTemplate
import io.github.karlatemp.klib.formatter.Template
import java.util.concurrent.ConcurrentHashMap

@Suppress("MemberVisibilityCanBePrivate")
class LocaleI18n(
    val formatter: Formatter = FEFormatter,
    var parent: LocaleI18n? = null,
    val templates: MutableMap<String, String> = HashMap(),
    val path: String = "\$unknown"
) {
    private val templateCaches = ConcurrentHashMap<String, Template>()

    fun getDeclaredTemplate(key: String): Template? {
        templateCaches[key]?.let { return it }
        val tmp = templates[key] ?: return null
        return formatter.parse(tmp).also {
            templateCaches[key] = it
        }
    }

    fun getTemplate(key: String): Template? {
        return getDeclaredTemplate(key) ?: parent?.getTemplate(key)
    }

    fun clearCaches() {
        templateCaches.clear()
        parent?.clearCaches()
    }

    operator fun get(key: String): Template {
        return getTemplate(key) ?: NoneTemplate(key)
    }

    override fun toString(): String {
        return "LocaleI18n{$path}"
    }
}
