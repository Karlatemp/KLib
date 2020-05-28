/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/04/19 21:23:47
 *
 * GitGPGAutomaticSignature/GitGPGAutomaticSignature/RAFOutputStream.kt
 */

package io.github.karlatemp.klib

import java.io.*

open class RAFOutputStream(
    var raf: RandomAccessFile?
) : OutputStream(), DataOutput {
    constructor(path: String, mode: String) : this(RandomAccessFile(path, mode))
    constructor(path: File, mode: String) : this(RandomAccessFile(path, mode))

    init {
        raf ?: error("Random Access File missing.")
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun raf() = raf ?: throw IOException("Stream closed")

    @Throws(IOException::class)
    override fun writeShort(v: Int) {
        raf().writeShort(v)
    }

    @Throws(IOException::class)
    override fun writeLong(v: Long) {
        raf().writeLong(v)
    }

    @Throws(IOException::class)
    override fun writeDouble(v: Double) {
        raf().writeDouble(v)
    }

    @Throws(IOException::class)
    override fun writeBytes(s: String) {
        raf().writeBytes(s)
    }

    @Throws(IOException::class)
    override fun writeByte(v: Int) {
        raf().writeByte(v)
    }

    @Throws(IOException::class)
    override fun writeFloat(v: Float) {
        raf().writeFloat(v)
    }

    @Throws(IOException::class)
    override fun write(b: Int) {
        raf().write(b)
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray) {
        raf().write(b)
    }

    @Throws(IOException::class)
    override fun write(b: ByteArray, off: Int, len: Int) {
        raf().write(b, off, len)
    }

    @Throws(IOException::class)
    override fun writeChars(s: String) {
        raf().writeChars(s)
    }

    @Throws(IOException::class)
    override fun writeChar(v: Int) {
        raf().writeChar(v)
    }

    @Throws(IOException::class)
    override fun writeBoolean(v: Boolean) {
        raf().writeBoolean(v)
    }

    @Throws(IOException::class)
    override fun writeUTF(s: String) {
        raf().writeUTF(s)
    }

    @Throws(IOException::class)
    override fun writeInt(v: Int) {
        raf().writeInt(v)
    }

    @Throws(IOException::class)
    override fun close() {
        raf?.apply {
            setLength(filePointer)
            close()
        }
        raf = null
    }

}