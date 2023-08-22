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
    private val declaredDatabaseVersion: Int,
    private val delegate: SupportSQLiteOpenHelper,
) : SupportSQLiteOpenHelper {

    class DatabaseCopyException(message: String, cause: Throwable? = null) :
        RuntimeException(message, cause)

    class DatabaseVersionMismatchException(message: String, cause: Throwable? = null) :
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
            verifyPrepackagedDatabaseVersion()
            if (!dbFile.exists()) {
                Timber.i("No on-device database file found at ${dbFile.absolutePath}")
                tryCopyingPrepackagedDatabase(dbFile)
            } else {
                Timber.i("Found on-device database file at ${dbFile.absolutePath}")
                verifyOnDeviceDatabaseVersion(dbFile)
            }
        } finally {
            copyLock.unlock()
        }
    }

    private fun verifyPrepackagedDatabaseVersion() {
        Timber.i("Verifying prepackaged database version...")
        try {
            val prepackagedDatabaseVersion =
                DbFiles.readDatabaseVersion(context.assets.open(assetsPath))
            if (prepackagedDatabaseVersion != declaredDatabaseVersion) {
                throw DatabaseVersionMismatchException(
                    "Prepackaged database version does not match declared database version (prepackaged = $prepackagedDatabaseVersion, declared = $declaredDatabaseVersion)"
                )
            }
        } catch (e: DbFiles.VersionReadException) {
            throw DatabaseCopyException("Unable to verify prepackaged database version", e)
        } catch (e: IOException) {
            throw DatabaseCopyException("Unable to verify prepackaged database version", e)
        }
    }

    private fun verifyOnDeviceDatabaseVersion(dbFile: File) {
        Timber.i("Verifying on-device database version...")
        val onDeviceVersion = tryReadingDatabaseVersion(dbFile)
        if (onDeviceVersion != declaredDatabaseVersion) {
            Timber.i("On-device database version does not math declared database version (on-device = $onDeviceVersion, declared = $declaredDatabaseVersion)")
            replaceOnDeviceDatabase(dbFile)
        } else {
            Timber.i("On-device database version is up to date (on-device version = $onDeviceVersion)")
        }
    }

    private fun replaceOnDeviceDatabase(dbFile: File) {
        Timber.i("Trying to replace on-device database...")
        if (context.deleteDatabase(databaseName)) {
            tryCopyingPrepackagedDatabase(dbFile)
        } else {
            throw DatabaseCopyException("Failed to delete the old version of the on-device database $databaseName")
        }
    }

    private fun tryCopyingPrepackagedDatabase(dbFile: File) {
        try {
            copyPrepackagedDatabase(dbFile)
        } catch (e: IOException) {
            throw DatabaseCopyException("Unable to copy prepackaged database file from assets", e)
        }
    }

    private fun copyPrepackagedDatabase(destinationFile: File) {
        Timber.i("Attempting to copy prepackaged database from assets...")

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

            if (!tempFile.renameTo(destinationFile)) {
                throw IOException("Failed to move temp file ${tempFile.absolutePath} to ${destinationFile.absolutePath}")
            }
            Timber.i("Prepackaged database has been successfully copied from assets")
        } finally {
            tempFile.delete()
        }
    }

    private fun tryReadingDatabaseVersion(dbFile: File): Int {
        return try {
            DbFiles.readDatabaseVersion(dbFile)
        } catch (e: DbFiles.VersionReadException) {
            throw DatabaseCopyException("Unable to read database version", e)
        }
    }

    override fun close() {
        delegate.close()
    }
}