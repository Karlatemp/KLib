/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/27 21:25:16
 *
 * KLib/KLib.main/I18nResourceParser.kt
 */

package io.github.karlatemp.klib.i18n

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.ValueType
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.yaml.snakeyaml.Yaml
import java.io.BufferedReader
import java.io.Reader
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap

typealias I18nResourceParser = Reader.() -> MutableMap<String, String>
typealias I18nResourceParserImplementable = (Reader) -> MutableMap<String, String>
//interface I18nResourceParser {
//    operator fun get(resource: Reader): MutableMap<String, String>?
//}

@Suppress("MemberVisibilityCanBePrivate")
object I18nResourceParsers {
    @JvmStatic
    private val parsers = ConcurrentHashMap<String, ParserRegistration>()

    class ParserRegistration internal constructor(
        val parser: I18nResourceParser,
        val type: String,
        private val lowerCastType: String
    ) {
        fun unregister(): Boolean {
            return parsers.remove(lowerCastType, this)
        }
    }

    @JvmStatic
    fun register(type: String, parser: I18nResourceParser): ParserRegistration? {
        val low = type.toLowerCase()
        val registration = ParserRegistration(parser, type, low)
        return parsers.putIfAbsent(low, registration).takeIf { it === registration }
    }

    @JvmStatic
    fun register(parser: I18nResourceParser, vararg types: String) {
        if (types.isEmpty()) throw IllegalArgumentException("Empty types.")
        types.forEach { register(it, parser) }
    }

    init {
        registerParsers()
    }

    @JvmStatic
    operator fun get(type: String) = parsers[type]?.parser
}

private fun registerParsers() {
    I18nResourceParsers.register("properties") {
        val properties = Properties()
        properties.load(this)
        HashMap<String, String>().also {
            properties.forEach { t, u ->
                it[t.toString()] = u.toString()
            }
        }
    }
    I18nResourceParsers.register("json", object : I18nResourceParserImplementable {
        val parser = JsonParser()
        override fun invoke(p1: Reader): MutableMap<String, String> {
            val result = HashMap<String, String>()
            val element = parser.parse(p1)
            resolve(result, element, LinkedList())
            return result
        }

        private fun resolve(
            result: MutableMap<String, String>,
            element: JsonElement,
            path: LinkedList<String>
        ) {
            when {
                element.isJsonNull -> {
                }
                element.isJsonArray -> {
                    element.asJsonArray.apply {
                        forEachIndexed { index: Int, jsonElement: JsonElement ->
                            path.addLast(index.toString())
                            resolve(result, jsonElement, path)
                            path.removeLast()
                        }
                    }
                }
                element.isJsonObject -> {
                    element.asJsonObject.apply {
                        entrySet().forEach { entry ->
                            path.addLast(entry.key)
                            resolve(result, entry.value, path)
                            path.removeLast()
                        }
                    }
                }
                element.isJsonPrimitive -> {
                    result[path.joinToString(".")] = element.asString
                }
                else -> error("Invalid json element")
            }
        }
    })
    I18nResourceParsers.register(object : I18nResourceParserImplementable {
        val yaml = Yaml()

        override fun invoke(p1: Reader): MutableMap<String, String> {
            val data = yaml.load<MutableMap<String, Any>>(p1)
            val result = HashMap<String, String>()
            resolve(data, result, LinkedList())
            return result
        }

        private fun resolve(data: Any?, result: MutableMap<String, String>, path: LinkedList<String>) {
            when (data) {
                is Map<*, *> -> {
                    data.entries.forEach { entry ->
                        path.addLast(entry.key.toString())
                        resolve(entry.value, result, path)
                        path.removeLast()
                    }
                }
                is List<*> -> {
                    data.forEachIndexed { index: Int, value: Any? ->
                        path.addLast(index.toString())
                        resolve(value, result, path)
                        path.removeLast()
                    }
                }
                else -> {
                    result[path.joinToString(".")] = data.toString()
                }
            }
        }
    }, "yaml", "yml")
    I18nResourceParsers.register("conf", object : I18nResourceParserImplementable {
        override fun invoke(p1: Reader): MutableMap<String, String> {
            val hocon = HoconConfigurationLoader.builder()
                .setSource { BufferedReader(p1) }
                .build()
                .load()
            val result = hashMapOf<String, String>()
            resolve(hocon, result)
            return result
        }

        private fun resolve(conf: ConfigurationNode, result: MutableMap<String, String>) {

            when (conf.valueType) {
                ValueType.MAP,
                ValueType.LIST -> {
                    if (conf.hasListChildren())
                        conf.childrenList.forEach { configurationNode ->
                            resolve(configurationNode, result)
                        }
                    if (conf.hasMapChildren())
                        conf.childrenMap.values.forEach { configurationNode ->
                            resolve(configurationNode, result)
                        }
                }
                ValueType.NULL -> {
                    // NULL!
                }
                ValueType.SCALAR -> {
                    result[conf.path.joinToString(".") { it.toString() }] = conf.value.toString()
                }
            }
        }
    })
}
