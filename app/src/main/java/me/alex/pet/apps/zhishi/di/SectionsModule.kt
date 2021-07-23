package me.alex.pet.apps.zhishi.di

import com.squareup.moshi.Moshi
import me.alex.pet.apps.zhishi.data.Database
import me.alex.pet.apps.zhishi.data.sections.SectionsDataStore
import me.alex.pet.apps.zhishi.domain.sections.SectionsRepository
import me.alex.pet.apps.zhishi.presentation.section.SectionViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sectionsModule = module {

    single { createSectionsRepository(get(), get()) }

    viewModel { (sectionId: Long) -> SectionViewModel(sectionId, get(), get()) }
}

private fun createSectionsRepository(db: Database, moshi: Moshi): SectionsRepository {
    return SectionsDataStore(db.sectionQueries, db.ruleQueries, moshi)
}