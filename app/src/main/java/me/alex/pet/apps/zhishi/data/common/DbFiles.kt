package me.alex.pet.apps.zhishi.data.common

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer

object DbFiles {
    fun readDatabaseVersion(dbFile: File): Int {
        return FileInputStream(dbFile).channel.use { input ->
            input.tryLock(60, 4, true)
            input.position(60)
            val buffer = ByteBuffer.allocate(4)
            val numOfReadBytes = input.read(buffer)
            if (numOfReadBytes != 4) {
                throw IOException("Bad database header, unable to read 4 bytes at offset 60")
            }
            buffer.rewind()
            buffer.int
        }
    }
}