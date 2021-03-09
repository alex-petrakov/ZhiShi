package me.alex.pet.apps.zhishi.domain

import me.alex.pet.apps.zhishi.domain.contents.Contents
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.Section
import me.alex.pet.apps.zhishi.domain.search.SearchResult

interface RulesRepository {

    suspend fun getContents(): Contents

    suspend fun getSection(sectionId: Long): Section?

    suspend fun getRule(ruleId: Long): Rule?

    suspend fun query(searchTerms: List<String>, limit: Int): List<SearchResult>
}