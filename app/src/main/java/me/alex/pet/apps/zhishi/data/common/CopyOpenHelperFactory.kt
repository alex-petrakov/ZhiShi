package me.alex.pet.apps.zhishi.data.common

import androidx.sqlite.db.SupportSQLiteOpenHelper
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory

class CopyOpenHelperFactory(private val assetsPath: String) : SupportSQLiteOpenHelper.Factory {
    override fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
        val frameworkOpenHelper = RequerySQLiteOpenHelperFactory().create(configuration)
        return CopyOpenHelper(
                configuration.context,
                assetsPath,
                configuration.callback.version,
                frameworkOpenHelper
        )
    }
}