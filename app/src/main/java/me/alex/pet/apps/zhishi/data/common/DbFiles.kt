package me.alex.pet.apps.zhishi.data.common

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object DbFiles {

    class VersionReadException(message: String, cause: Throwable? = null) :
        RuntimeException(message, cause)

    @Throws(VersionReadException::class)
    fun readDatabaseVersion(dbFile: File): Int {
        return readDatabaseVersion(FileInputStream(dbFile))
    }

    @Throws(VersionReadException::class)
    fun readDatabaseVersion(inputStream: InputStream): Int {
        val arr = ByteArray(4)
        val readBytes = inputStream.buffered().use { input ->
            try {
                input.skip(60)
                input.read(arr)
            } catch (e: IOException) {
                throw VersionReadException(
                    "An IO error occurred while trying to read database version", e
                )
            }
        }
        if (readBytes != 4) {
            throw VersionReadException("Bad database header, unable to read 4 bytes at offset 60")
        }
        return ByteBuffer.wrap(arr).order(ByteOrder.BIG_ENDIAN).int
    }
}