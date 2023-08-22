package me.alex.pet.apps.zhishi.data.common

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock


class CopyOpenHelper(
    private val context: Context,
    private val assetsPath: String,
    private val databaseVersion: Int,
    private val delegate: SupportSQLiteOpenHelper,
) : SupportSQLiteOpenHelper {

    class DatabaseCopyException(message: String, cause: Throwable? = null) :
        RuntimeException(message, cause)

    override val databaseName: String?
        get() = delegate.databaseName

    override val writableDatabase: SupportSQLiteDatabase
        get() {
            verifyDatabase()
            return delegate.writableDatabase
        }

    override val readableDatabase: SupportSQLiteDatabase
        get() {
            verifyDatabase()
            return delegate.readableDatabase
        }

    override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
        delegate.setWriteAheadLoggingEnabled(enabled)
    }

    private fun verifyDatabase() {
        Timber.i("Verifying database file...")
        val dbFile: File = context.getDatabasePath(databaseName)
        val copyLock = ReentrantLock()
        try {
            copyLock.lock()
            if (!dbFile.exists()) {
                Timber.i("No database file found at ${dbFile.absolutePath}")
                tryCopyDatabaseFileFromAssets(dbFile)
            } else {
                Timber.i("Found database file ${dbFile.absolutePath}")
                verifyDatabaseVersion(dbFile)
            }
        } finally {
            copyLock.unlock()
        }
    }

    private fun verifyDatabaseVersion(dbFile: File) {
        Timber.i("Verifying existing database version...")
        val currentVersion = tryReadDatabaseVersion(dbFile)
        if (currentVersion != databaseVersion) {
            Timber.i("Database version is not up to date (current version = $currentVersion, required version = $databaseVersion)")
            replaceExistingDatabase(dbFile)
        } else {
            Timber.i("Database version is up to date (current version = $currentVersion)")
        }
    }

    private fun replaceExistingDatabase(dbFile: File) {
        Timber.i("Trying to replace existing database...")
        if (context.deleteDatabase(databaseName)) {
            tryCopyDatabaseFileFromAssets(dbFile)
        } else {
            throw DatabaseCopyException("Failed to delete old version of the database file $databaseName")
        }
    }

    private fun tryCopyDatabaseFileFromAssets(dbFile: File) {
        try {
            copyDatabaseFileFromAssets(dbFile)
        } catch (e: IOException) {
            throw DatabaseCopyException("Unable to copy database file from assets", e)
        }
    }

    private fun copyDatabaseFileFromAssets(destinationFile: File) {
        Timber.i("Attempting to copy the database from the assets...")

        val tempFile = File.createTempFile("rules-copy-helper", ".tmp", context.cacheDir)
            .also { it.deleteOnExit() }
        try {
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

            val assetsDbVersion = DbFiles.readDatabaseVersion(tempFile)
            if (assetsDbVersion != databaseVersion) {
                throw IOException("Assets contain DB with unexpected version (expected = $databaseVersion, actual = $assetsDbVersion)")
            }

            if (!tempFile.renameTo(destinationFile)) {
                throw IOException("Failed to move temp file ${tempFile.absolutePath} to ${destinationFile.absolutePath}")
            }
            Timber.i("Database has been successfully copied from assets")
        } finally {
            tempFile.delete()
        }
    }

    private fun tryReadDatabaseVersion(dbFile: File): Int {
        return try {
            DbFiles.readDatabaseVersion(dbFile)
        } catch (e: IOException) {
            throw DatabaseCopyException("Unable to read database version", e)
        }
    }

    override fun close() {
        delegate.close()
    }
}