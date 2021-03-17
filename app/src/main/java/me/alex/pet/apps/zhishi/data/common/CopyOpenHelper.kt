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

    class DatabaseCopyException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

    override fun getDatabaseName(): String? {
        return delegate.databaseName
    }

    override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
        delegate.setWriteAheadLoggingEnabled(enabled)
    }

    override fun getWritableDatabase(): SupportSQLiteDatabase {
        verifyDatabase(true)
        return delegate.writableDatabase
    }

    override fun getReadableDatabase(): SupportSQLiteDatabase {
        verifyDatabase(false)
        return delegate.readableDatabase
    }

    private fun verifyDatabase(writable: Boolean) {
        val dbFile: File = context.getDatabasePath(databaseName)

        // TODO: prevent concurrent writes?
        if (!dbFile.exists()) {
            Timber.i("No database file found at ${dbFile.absolutePath}")
            tryCopyDatabaseFile(dbFile, writable)
            return
        }

        val currentVersion = tryReadDatabaseVersion(dbFile)
        Timber.i("Found database file $databaseName with version $currentVersion")

        if (currentVersion == databaseVersion) {
            Timber.i("Database version is up to date (current version = $currentVersion)")
            return
        }

        Timber.i("Database version is not up to date (current version = $currentVersion, required version = $databaseVersion)")
        if (context.deleteDatabase(databaseName)) {
            tryCopyDatabaseFile(dbFile, writable)
        } else {
            throw DatabaseCopyException("Failed to delete old version of the database file $databaseName")
        }
    }

    private fun tryCopyDatabaseFile(dbFile: File, writable: Boolean) {
        try {
            copyDatabaseFileFromAssets(dbFile, writable)
        } catch (e: IOException) {
            throw DatabaseCopyException("Unable to copy database file", e)
        }
    }

    private fun copyDatabaseFileFromAssets(destinationFile: File, writable: Boolean) {
        Timber.i("Attempting to copy the database from the assets")

        val tempFile = File.createTempFile(
                "database",
                ".tmp",
                context.cacheDir
        ).also { it.deleteOnExit() }

        context.assets.open(assetsPath).use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                val byteCount = inputStream.copyTo(outputStream)
                Timber.i("Copied $byteCount B from assets to temp file")
            }
        }

        val parent = destinationFile.parentFile
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw IOException("Failed to create parent directories for ${destinationFile.absolutePath}")
        }

        // TODO: try to open the db?

        if (!tempFile.renameTo(destinationFile)) {
            throw IOException("Failed to move temp file ${tempFile.absolutePath} to ${destinationFile.absolutePath}")
        }
        Timber.i("Database has been successfully copied from assets")
    }

    private fun tryReadDatabaseVersion(dbFile: File): Int {
        return try {
            readVersion(dbFile)
        } catch (e: IOException) {
            throw DatabaseCopyException("Unable to read database version", e)
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

    override fun close() {
        delegate.close()
    }
}