/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/27 18:22:47
 *
 * KLib/KLib.main/PluginI18nLoader.kt
 */

package io.github.karlatemp.klib.i18n

import io.github.karlatemp.klib.formatter.FEFormatter
import io.github.karlatemp.klib.formatter.Formatter
import io.github.karlatemp.klib.plugindata.metadataOf
import io.github.karlatemp.klib.reflect.jarFile
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.Reader
import java.util.*
import java.util.logging.Level
import java.util.zip.ZipFile
import kotlin.collections.HashMap

class PluginI18nLoader(
    val formatter: Formatter,
    val plugin: JavaPlugin
) {
    private val logger = plugin.logger
    val i18n = HashMap<String, LocaleI18n>()

    private fun ZipFile.scanJar() {
        entries().iterator().forEach { entry ->
            if (entry.isDirectory) return@forEach
            if (entry.name.startsWith("i18n/")) {
                val name = entry.name.substring(5)
                InputStreamReader(getInputStream(entry), Charsets.UTF_8).use {
                    it.loadI18n(name, "\$zip!/${entry.name}")
                }
            }
        }
    }

    private fun File.scanDir() {
        walk(direction = FileWalkDirection.BOTTOM_UP).maxDepth(1).forEach { file ->
            if (file.isFile)
                InputStreamReader(FileInputStream(file), Charsets.UTF_8).use {
                    it.loadI18n(file.name, "\$dir!/${file.name}")
                }
        }
    }

    private fun Reader.loadI18n(name: String, path: Any) {
        logger.fine("Loading i18n with name $name from $path")
        kotlin.runCatching {
            val splitter = name.lastIndexOf('.')
            if (splitter == -1) {
                logger.warning("No ext for i18n file: $path")
                return
            }
            val type = name.substring(splitter + 1)
            val name0 = name.substring(0, splitter)
            val parser = I18nResourceParsers[type] ?: kotlin.run {
                logger.warning("Error in loading $path: No i18n loader of $type found.")
                return@loadI18n
            }
            val translates = parser.invoke(this)
            val li = LocaleI18n(formatter, i18n[name0], translates, path.toString())
            i18n[name0] = li
        }.onFailure {
            logger.log(Level.WARNING, "Error in loading i18n file: $path", it)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun reload() {
        empty.templates.clear()
        i18n.clear()
        ZipFile(plugin.jarFile).use { it.scanJar() }
        File(plugin.dataFolder, "i18n").also { it.mkdirs() }.scanDir()
        fixLink()
    }


    private val empty = LocaleI18n()

    init {
        reload()
    }

    private fun fixLink() {
        val root = i18n["root"]
        i18n.forEach { (t, u) ->
            if (t != "root") {
                u.mklink(t, root)
            }
        }
    }

    private tailrec fun LocaleI18n.mklink(t: String, root: LocaleI18n?) {
        val lst = t.lastIndexOf('_')
        if (lst == -1) {
            appendParent(root)
        } else {
            val pky = t.substring(0, lst)
            val p = i18n[pky]
            if (p != null) {
                appendParent(p)
            } else {
                mklink(pky, root)
            }
        }
    }

    private tailrec fun LocaleI18n.appendParent(root: LocaleI18n?) {
        if (parent != null) {
            parent!!.appendParent(root)
            return
        } else {
            parent = root
        }
    }

    override fun toString(): String {
        val rootView = SubView()
        val views = mutableMapOf<LocaleI18n, SubView>()
        i18n.values.forEach { i18 ->
            if (i18.parent == null) {
                rootView.addLast(i18)
            } else {
                views.computeIfAbsent(i18.parent!!) { SubView() }.add(i18)
            }
        }
        return buildString {
            fun String.make(view: SubView) {
                view.forEach { i18 ->
                    append(this)
                    append(i18)
                    val sv = views[i18]
                    if (!(sv == null || sv.isEmpty())) {
                        append(":\n")
                        "$this  ".make(sv)
                    } else {
                        append('\n')
                    }
                }
            }
            "".make(rootView)
        }.let {
            if (it.isEmpty()) return@let "I18nLoader{<None i18n>}"
            it
        }
    }

    operator fun get(key: String): LocaleI18n = get0(key.toLowerCase())

    private tailrec fun get0(key: String): LocaleI18n {
        val v = i18n[key]
        if (v != null) return v
        if (key == "root") {
            return empty
        }
        val splitter = key.lastIndexOf('_')
        if (splitter == -1) return get0("root")
        return get0(key.substring(0, splitter))
    }
}

private typealias SubView = LinkedList<LocaleI18n>

val JavaPlugin.i18nLoader: PluginI18nLoader
    get() {
        return metadataOf(this).computeIfAbsent("klib.i18n-loader") {
            PluginI18nLoader(FEFormatter, this)
        } as PluginI18nLoader
    }

val JavaPlugin.i18n: LocaleI18n
    get() {
        val metadata = metadataOf(this)
        metadata["klib.i18n"]?.apply { return this@apply as LocaleI18n }

        return i18nLoader.let { loader ->
            val lang = metadata["language"]?.toString()
                ?: this.config.getString("language")
                ?: Locale.getDefault().toString()
            return@let loader[lang].also { metadata["klib.i18n"] = it }
        }
    }
