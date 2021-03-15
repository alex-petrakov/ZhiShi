package me.alex.pet.apps.zhishi.data.common

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CopyOpenHelper(
        private val context: Context,
        private val assetsPath: String,
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
        val dbFile = context.getDatabasePath(databaseName)

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
        // TODO: check the DB version
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