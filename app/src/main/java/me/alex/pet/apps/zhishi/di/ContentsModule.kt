package me.alex.pet.apps.zhishi.di

import com.squareup.moshi.Moshi
import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.contents.ContentsDataStore
import me.alex.pet.apps.zhishi.domain.contents.ContentsRepository
import me.alex.pet.apps.zhishi.presentation.contents.ContentsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val contentsModule = module {

    single { createContentsRepository(get(), get()) }

    viewModel { ContentsViewModel(get(), get()) }
}

private fun createContentsRepository(db: Database, moshi: Moshi): ContentsRepository {
    return ContentsDataStore(
            db.partQueries,
            db.chapterQueries,
            db.sectionQueries,
            moshi
    )
}