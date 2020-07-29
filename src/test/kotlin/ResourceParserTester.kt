import io.github.karlatemp.klib.i18n.I18nResourceParsers
import org.intellij.lang.annotations.Language
import org.junit.Test
import java.io.StringReader
import kotlin.test.assertTrue

/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/27 21:55:31
 *
 * KLib/KLib.test/ResourceParserTester.kt
 */

class ResourceParserTester {
    @Test
    fun testHocon() {
        @Language("conf")
        val hocon = """
hi = Hello
test = [
  {
    hi = "hi!"
  },
  "FAQ",
  {fa = "Q"}
]
hum {
  hu: "FU"
}
        """
        val parser = I18nResourceParsers["conf"] ?: error("Hocon not found")
        val result = parser.invoke(StringReader(hocon))
        result.forEach { (t, u) ->
            println("\"$t\" to \"$u\",")
        }
    }

    @Test
    fun testYaml() {
        println("== == == ==[ YAML ]== == == ==")
        val parser = I18nResourceParsers["yaml"] ?: error("Yaml not found")

        @Language("yaml")
        val yaml = """
hi: Hello World
list:
- K1
- K2
- K3
mapWithList:
- hi: Fa
- sb: sb
- 粘兽: 傻逼
- a:
  - sb: SB
洛:
  洛必达:
    "洛必达!": 洛不出来
"""
        val result = parser.invoke(StringReader(yaml))
        result.forEach { (t, u) ->
            println("\"$t\" to \"$u\",")
        }
        assertTrue {
            result == mapOf(
                "hi" to "Hello World",
                "洛.洛必达.洛必达!" to "洛不出来",
                "mapWithList.2.粘兽" to "傻逼",
                "mapWithList.3.a.0.sb" to "SB",
                "mapWithList.0.hi" to "Fa",
                "mapWithList.1.sb" to "sb",
                "list.1" to "K2",
                "list.0" to "K1",
                "list.2" to "K3"
            )
        }
    }

    @Test
    fun testJson() {
        println("== == == ==[ JSON ]== == == ==")
        @Language("json") val json = """
{
  "hi": "Hello World!",
  "洛必达": {"洛":  "洛不出来"},
  "list": ["faq","awa",{
    "洛必达": "洛必达"
  }],
  "粘兽": "傻逼"
}
"""
        val result = I18nResourceParsers["json"]!!.invoke(StringReader(json))
        result.forEach { (t, u) ->
            println("\"$t\" to \"$u\",")
        }
        assertTrue {
            result == mapOf(
                "hi" to "Hello World!",
                "洛必达.洛" to "洛不出来",
                "list.1" to "awa",
                "list.0" to "faq",
                "list.2.洛必达" to "洛必达",
                "粘兽" to "傻逼"
            )
        }
    }
}