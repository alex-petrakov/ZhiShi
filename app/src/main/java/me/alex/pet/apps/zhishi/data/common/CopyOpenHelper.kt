package me.alex.pet.apps.zhishi.data.common

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

class CopyOpenHelper(
        private val context: Context,
        private val assetsPath: String,
        private val databaseVersion: Int,
        private val delegate: SupportSQLiteOpenHelper
) : SupportSQLiteOpenHelper {

    override fun getDatabaseName(): String? {
        return delegate.databaseName
    }

    override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
        delegate.setWriteAheadLoggingEnabled(enabled)
    }

    override fun getWritableDatabase(): SupportSQLiteDatabase {
        Timber.d("getWritableDatabase()")

        prepareDatabase(true)

        return delegate.writableDatabase
    }

    override fun getReadableDatabase(): SupportSQLiteDatabase {
        Timber.d("getReadableDatabase()")

        prepareDatabase(false)

        return delegate.readableDatabase
    }

    private fun prepareDatabase(writable: Boolean) {
        val dbFile: File = context.getDatabasePath(databaseName)

        // TODO: prevent concurrent writes?
        if (!dbFile.exists()) {
            Timber.i("No database file found at ${dbFile.absolutePath}")
            try {
                copyDatabaseFile(dbFile, writable)
                return
            } catch (e: IOException) {
                throw RuntimeException("Unable to copy database file.", e)
            }
        }

        val currentVersion = try {
            readVersion(dbFile)
        } catch (e: IOException) {
            Timber.w(e, "Unable to read database version")
            return
        }
        Timber.i("Found database file $databaseName with version $currentVersion")

        if (currentVersion == databaseVersion) {
            Timber.i("Database version is up to date (current version = $currentVersion)")
            return
        }

        Timber.i("Database version is not up to date (current version = $currentVersion, required version = $databaseVersion)")
        if (context.deleteDatabase(databaseName)) {
            try {
                copyDatabaseFile(dbFile, writable)
            } catch (e: IOException) {
                throw RuntimeException("Unable to copy database file during version migration", e)
            }
        } else {
            throw RuntimeException("Failed to delete old version of the database file $databaseName")
        }
    }

    private fun readVersion(dbFile: File): Int {
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

    private fun copyDatabaseFile(destinationFile: File, writable: Boolean) {
        Timber.i("Attempting to copy the database from the assets")

        val tempFile = File.createTempFile(
                "database",
                ".tmp",
                context.cacheDir
        ).also { it.deleteOnExit() }

        context.assets.open(assetsPath).use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        val parent = destinationFile.parentFile
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw IOException("Failed to create parent directories for ${destinationFile.absolutePath}")
        }

        // TODO: try to open the db?

        if (!tempFile.renameTo(destinationFile)) {
            throw IOException("Failed to move intermediate file ${tempFile.absolutePath} to ${destinationFile.absolutePath}")
        }
    }

    override fun close() {
        delegate.close()
    }
}