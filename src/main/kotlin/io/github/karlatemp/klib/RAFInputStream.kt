/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/05/28 18:05:53
 *
 * KLib/KLib.main/RAFInputStream.kt
 */

package io.github.karlatemp.klib

import java.io.*

open class RAFInputStream(
    var raf: RandomAccessFile?
) : InputStream(), DataInput {
    constructor(path: String, mode: String) : this(RandomAccessFile(path, mode))
    constructor(path: File, mode: String) : this(RandomAccessFile(path, mode))

    init {
        raf ?: error("Random Access File missing.")
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun raf() = raf ?: throw IOException("Stream closed")

    @Throws(IOException::class)
    override fun read(): Int {
        return raf().read()
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray): Int {
        return raf().read(b)
    }

    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        return raf().read(b, off, len)
    }

    @Throws(IOException::class)
    override fun close() {
        raf?.apply {
            setLength(filePointer)
            close()
        }
        raf = null
    }

    @Throws(IOException::class)
    override fun readFully(b: ByteArray) {
        raf().readFully(b)
    }

    @Throws(IOException::class)
    override fun readFully(b: ByteArray, off: Int, len: Int) {
        raf().readFully(b, off, len)
    }

    @Throws(IOException::class)
    override fun readInt(): Int {
        return raf().readInt()
    }

    @Throws(IOException::class)
    override fun readUnsignedShort(): Int {
        return raf().readUnsignedShort()
    }

    @Throws(IOException::class)
    override fun readUnsignedByte(): Int {
        return raf().readUnsignedByte()
    }

    @Throws(IOException::class)
    override fun readUTF(): String {
        return raf().readUTF()
    }

    @Throws(IOException::class)
    override fun readChar(): Char {
        return raf().readChar()
    }

    @Throws(IOException::class)
    override fun readLine(): String {
        return raf().readLine()
    }

    @Throws(IOException::class)
    override fun readByte(): Byte {
        return raf().readByte()
    }

    @Throws(IOException::class)
    override fun readFloat(): Float {
        return raf().readFloat()
    }

    @Throws(IOException::class)
    override fun skipBytes(n: Int): Int {
        return raf().skipBytes(n)
    }

    @Throws(IOException::class)
    override fun readLong(): Long {
        return raf().readLong()
    }

    @Throws(IOException::class)
    override fun readDouble(): Double {
        return raf().readDouble()
    }

    @Throws(IOException::class)
    override fun readBoolean(): Boolean {
        return raf().readBoolean()
    }

    @Throws(IOException::class)
    override fun readShort(): Short {
        return raf().readShort()
    }

}