import io.github.karlatemp.klib.ConcurrentLinkedList
import org.apache.commons.lang.math.RandomUtils
import org.junit.Test
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.test.assertTrue

/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/14 22:28:17
 *
 * KLib/KLib.test/LinkedListTest.kt
 */

class LinkedListTest {
    @Test
    fun testCopyOnWrite() {
        val link = CopyOnWriteArrayList<Int>()
        val counter = AtomicInteger()
        val threads = LinkedList<Thread>()
        val ending = 100000
        repeat(50) {
            thread {
                while (true) {
                    val count = counter.getAndIncrement()
                    if (count > ending) break
                    link.add(count)
                }
            }.let { threads.add(it) }
        }
        threads.forEach { it.join() }
    }

    @Test
    fun testCor() {
        val counter = AtomicInteger()
        val threads = LinkedList<Thread>()
        val link = ConcurrentLinkedList<Int>()
        val ending = 100000
        repeat(50) {
            thread {
                while (true) {
                    val count = counter.getAndIncrement()
                    if (count > ending) break
                    if (RandomUtils.nextBoolean())
                        link.insertLast(count)
                    else link.insertFirst(count)
                }
            }.let { threads.add(it) }
        }
        threads.forEach { it.join() }
        val linked = link.mapTo(LinkedList()) { it }
        linked.sort()
        for (i in 0..ending) {
            val next = linked.poll()
            if (next != i) {
                throw AssertionError("Assert failed: $i, $next")
            }
        }
        assertTrue { linked.isEmpty() }
    }

    @Test
    fun test() {
        val link = ConcurrentLinkedList<String>()
        println(link)
        link.insertFirst("First")
        println(link)
        link.insertFirst("First st2")
        println(link)
        link.insertLast("Last")
        println(link)
        link.insertLast("Last st2")
        println(link)
        val ite = (link.iterator() as MutableIterator<String>)
        while (ite.hasNext()) {
            if (ite.next() == "Last") ite.remove()
        }
        println(link)
    }
}