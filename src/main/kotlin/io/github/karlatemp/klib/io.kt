/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/07/21 16:44:16
 *
 * KLib/KLib.main/io.kt
 */

package io.github.karlatemp.klib

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class UnclosedInputStream(`in`: InputStream) : InputStream() {
    private var inp: InputStream? = `in`
    private val i: InputStream get() = inp ?: throw IOException("Stream closed.")
    override fun read(): Int = i.read()

    override fun read(b: ByteArray): Int = i.read(b)


    override fun read(b: ByteArray, off: Int, len: Int): Int = i.read(b, off, len)


    override fun available(): Int = i.available()


    override fun close() {
        inp = null
    }

    override fun mark(readlimit: Int) =
        i.mark(readlimit)


    override fun markSupported(): Boolean = inp?.markSupported() ?: false

    override fun reset() = i.reset()


    override fun skip(n: Long): Long = i.skip(n)
    override fun skipNBytes(n: Long) = i.skipNBytes(n)
}

class UnclosedOutputStream(out: OutputStream) : OutputStream() {
    private var out: OutputStream? = out
    override fun close() {
        flush()
        out = null
    }

    private val o: OutputStream get() = out ?: throw IOException("Stream closed")

    override fun write(b: Int) {
        o.write(b)
    }

    override fun write(b: ByteArray) {
        o.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        o.write(b, off, len)
    }

    override fun flush() {
        o.flush()
    }
}