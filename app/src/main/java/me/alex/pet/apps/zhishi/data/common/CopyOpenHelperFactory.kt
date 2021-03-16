package me.alex.pet.apps.zhishi.data.common

import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory

class CopyOpenHelperFactory(val assetsPath: String, val databaseVersion: Int) : SupportSQLiteOpenHelper.Factory {
    override fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
        val frameworkOpenHelper = FrameworkSQLiteOpenHelperFactory().create(configuration)
        return CopyOpenHelper(configuration.context, assetsPath, databaseVersion, frameworkOpenHelper)
    }
}