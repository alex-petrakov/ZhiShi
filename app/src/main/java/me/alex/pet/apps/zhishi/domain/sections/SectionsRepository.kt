package me.alex.pet.apps.zhishi.domain.sections

interface SectionsRepository {

    suspend fun getSection(sectionId: Long): Section?
}