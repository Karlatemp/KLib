import io.github.karlatemp.klib.I18n
import io.github.karlatemp.klib.formatter.FEFormatter
import io.github.karlatemp.klib.formatter.LinkedTemplate
import org.junit.Test

/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/13 21:52:42
 *
 * KLib/KLib.test/FormatterTest.kt
 */

class FormatterTest {
    @Test
    fun testFormatParsing() {
        FEFormatter.parse(
            "Hello {0} Wel come to {kotlin script} aw{"
        ).also {
            (it as LinkedTemplate).actions.forEach { action ->
                println(action)
            }
        }.doFormat(StringBuilder(), "AWA").let {
            println(it)
        }
    }

    fun a() {
        I18n { key -> null }["message.welcome"].get()
    }
}