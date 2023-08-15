package me.alex.pet.apps.zhishi.data.common

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.api.expectThrows
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CopyOpenHelperTests {

    private val appContext: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun copies_the_prepackaged_DB_if_the_on_device_DB_does_not_exist() {
        deleteOnDeviceDatabase(appContext)
        val copyOpenHelper = createCopyOpenHelper(
            context = appContext,
            declaredDbVersion = 1,
            prepackagedDbVersion = 1,
        )

        copyOpenHelper.readableDatabase

        val onDeviceDb: File = appContext.getDatabasePath(DB_NAME)
        expectThat(onDeviceDb)
            .exists()
            .hasVersion(1)
    }

    @Test
    fun copies_the_prepackaged_DB_if_a_single_step_upgrade_is_required() {
        deleteOnDeviceDatabase(appContext)
        createOnDeviceDatabase(appContext, databaseVersion = 1)
        val copyOpenHelper = createCopyOpenHelper(
            context = appContext,
            declaredDbVersion = 2,
            prepackagedDbVersion = 2,
        )

        copyOpenHelper.readableDatabase

        val onDeviceDb: File = appContext.getDatabasePath(DB_NAME)
        expectThat(onDeviceDb)
            .exists()
            .hasVersion(2)
    }

    @Test
    fun copies_the_prepackaged_DB_if_a_multi_step_upgrade_is_required() {
        deleteOnDeviceDatabase(appContext)
        createOnDeviceDatabase(appContext, databaseVersion = 1)
        val copyOpenHelper = createCopyOpenHelper(
            context = appContext,
            declaredDbVersion = 3,
            prepackagedDbVersion = 3,
        )

        copyOpenHelper.readableDatabase

        val onDeviceDb: File = appContext.getDatabasePath(DB_NAME)
        expectThat(onDeviceDb)
            .exists()
            .hasVersion(3)
    }

    @Test
    fun copies_the_prepackaged_DB_if_a_single_step_downgrade_is_required() {
        deleteOnDeviceDatabase(appContext)
        createOnDeviceDatabase(appContext, databaseVersion = 2)
        val copyOpenHelper = createCopyOpenHelper(
            context = appContext,
            declaredDbVersion = 1,
            prepackagedDbVersion = 1,
        )

        copyOpenHelper.readableDatabase

        val onDeviceDb: File = appContext.getDatabasePath(DB_NAME)
        expectThat(onDeviceDb)
            .exists()
            .hasVersion(1)
    }

    @Test
    fun copies_the_prepackaged_DB_if_a_multi_step_downgrade_is_required() {
        deleteOnDeviceDatabase(appContext)
        createOnDeviceDatabase(appContext, databaseVersion = 4)
        val copyOpenHelper = createCopyOpenHelper(
            context = appContext,
            declaredDbVersion = 2,
            prepackagedDbVersion = 2,
        )

        copyOpenHelper.readableDatabase

        val onDeviceDb: File = appContext.getDatabasePath(DB_NAME)
        expectThat(onDeviceDb)
            .exists()
            .hasVersion(2)
    }

    @Test
    fun throws_on_upgrade_if_the_prepackaged_DB_has_invalid_version() {
        deleteOnDeviceDatabase(appContext)
        createOnDeviceDatabase(appContext, databaseVersion = 1)
        val copyOpenHelper = createCopyOpenHelper(
            context = appContext,
            declaredDbVersion = 2,
            prepackagedDbVersion = 1,
        )

        expectThrows<CopyOpenHelper.DatabaseCopyException> {
            copyOpenHelper.readableDatabase
        }

        val onDeviceDb: File = appContext.getDatabasePath(DB_NAME)
        expectThat(onDeviceDb).doesNotExist()
    }

    @Test
    fun throws_on_downgrade_if_the_prepackaged_DB_has_invalid_version() {
        deleteOnDeviceDatabase(appContext)
        createOnDeviceDatabase(appContext, databaseVersion = 2)
        val copyOpenHelper = createCopyOpenHelper(
            context = appContext,
            declaredDbVersion = 1,
            prepackagedDbVersion = 2,
        )

        expectThrows<CopyOpenHelper.DatabaseCopyException> {
            copyOpenHelper.readableDatabase
        }

        val onDeviceDb: File = appContext.getDatabasePath(DB_NAME)
        expectThat(onDeviceDb).doesNotExist()
    }
}

private const val DB_NAME = "rules.db"

private fun deleteOnDeviceDatabase(context: Context) {
    val dbFile: File = context.getDatabasePath(DB_NAME)
    if (dbFile.exists() && !context.deleteDatabase(DB_NAME)) {
        error("Unable to delete database $DB_NAME")
    }
}

private fun createOnDeviceDatabase(context: Context, databaseVersion: Int) {
    val databaseFile = context.getDatabasePath(DB_NAME)
    val parentDir = databaseFile.parentFile
    if (parentDir != null && !parentDir.isDirectory && !parentDir.mkdirs()) {
        throw IllegalStateException("Failed to create parent directories for ${databaseFile.absolutePath}")
    }

    val dbAssetPath = getTestDatabaseAssetsPath(databaseVersion)
    try {
        context.assets.open(dbAssetPath).use { inputStream ->
            FileOutputStream(databaseFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    } catch (e: IOException) {
        throw IllegalStateException(
            "Unable to copy the database from the assets to the device. Make sure that the database file does exist at 'debug/assets/$dbAssetPath'.",
            e
        )
    }
}

private fun getTestDatabaseAssetsPath(databaseVersion: Int) = "db/test_db_v$databaseVersion.db"

private fun createCopyOpenHelper(
    context: Context,
    declaredDbVersion: Int,
    prepackagedDbVersion: Int,
): SupportSQLiteOpenHelper {
    val prepackagedDbPath = getTestDatabaseAssetsPath(prepackagedDbVersion)
    val config = SupportSQLiteOpenHelper.Configuration(
        context = context,
        name = DB_NAME,
        callback = NoOpCallback(declaredDbVersion)
    )
    return CopyOpenHelperFactory(prepackagedDbPath).create(config)
}

class NoOpCallback(declaredDbVersion: Int) : SupportSQLiteOpenHelper.Callback(declaredDbVersion) {

    override fun onCreate(db: SupportSQLiteDatabase) {
        // Do nothing
    }

    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Do nothing
    }
}

private fun <T : File> Assertion.Builder<T>.exists() =
    assert("exists") {
        if (it.exists()) pass() else fail()
    }

private fun <T : File> Assertion.Builder<T>.doesNotExist() =
    assert("does not exist") {
        if (!it.exists()) pass() else fail()
    }

private fun <T : File> Assertion.Builder<T>.hasVersion(expected: Int) =
    assert("has version %d", expected) {
        when (val actual = DbFiles.readDatabaseVersion(it)) {
            expected -> pass()
            else -> fail(actual)
        }
    }