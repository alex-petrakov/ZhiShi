package me.alex.pet.apps.zhishi.data.common

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock


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
        Timber.i("Verifying database file...")
        val dbFile: File = context.getDatabasePath(databaseName)
        val copyLock = ReentrantLock()
        try {
            copyLock.lock()
            if (!dbFile.exists()) {
                Timber.i("No database file found at ${dbFile.absolutePath}")
                tryCopyDatabaseFileFromAssets(dbFile, writable)
            } else {
                Timber.i("Found database file ${dbFile.absolutePath}")
                verifyDatabaseVersion(dbFile, writable)
            }
        } finally {
            copyLock.unlock()
        }
    }

    private fun verifyDatabaseVersion(dbFile: File, writable: Boolean) {
        Timber.i("Verifying existing database version...")
        val currentVersion = tryReadDatabaseVersion(dbFile)
        if (currentVersion != databaseVersion) {
            Timber.i("Database version is not up to date (current version = $currentVersion, required version = $databaseVersion)")
            replaceExistingDatabase(dbFile, writable)
        } else {
            Timber.i("Database version is up to date (current version = $currentVersion)")
        }
    }

    private fun replaceExistingDatabase(dbFile: File, writable: Boolean) {
        Timber.i("Trying to replace existing database...")
        if (context.deleteDatabase(databaseName)) {
            tryCopyDatabaseFileFromAssets(dbFile, writable)
        } else {
            throw DatabaseCopyException("Failed to delete old version of the database file $databaseName")
        }
    }

    private fun tryCopyDatabaseFileFromAssets(dbFile: File, writable: Boolean) {
        try {
            copyDatabaseFileFromAssets(dbFile, writable)
        } catch (e: IOException) {
            throw DatabaseCopyException("Unable to copy database file from assets", e)
        }
    }

    private fun copyDatabaseFileFromAssets(destinationFile: File, writable: Boolean) {
        Timber.i("Attempting to copy the database from the assets...")

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

        tryOpenDatabase(tempFile, writable)

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

    private fun tryOpenDatabase(databaseFile: File, writable: Boolean) {
        createFrameworkOpenHelper(databaseFile).use { helper ->
            if (writable) {
                helper.writableDatabase
            } else {
                helper.readableDatabase
            }
        }
    }

    private fun createFrameworkOpenHelper(databaseFile: File): SupportSQLiteOpenHelper {
        val databaseName = databaseFile.name
        val version = tryReadDatabaseVersion(databaseFile)
        val factory = FrameworkSQLiteOpenHelperFactory()
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(databaseName)
                .callback(object : SupportSQLiteOpenHelper.Callback(version) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        // Do nothing
                    }

                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                        // Do nothing
                    }
                })
                .build()
        return factory.create(configuration)
    }

    override fun close() {
        delegate.close()
    }
}