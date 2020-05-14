/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/14 22:04:06
 *
 * KLib/KLib.main/Atomic.kt
 */

package io.github.karlatemp.klib

import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty


operator fun <V> AtomicReference<V>.setValue(
    ignored0: Any, property: KProperty<*>, value: V
) {
    set(value)
}

operator fun <V> AtomicReference<V>.getValue(
    ignored: Any, property: KProperty<*>
): V {
    return get()
}
