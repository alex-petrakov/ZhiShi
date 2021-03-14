package me.alex.pet.apps.zhishi.domain.rules

import me.alex.pet.apps.zhishi.domain.contents.Contents
import me.alex.pet.apps.zhishi.domain.search.SearchResult

interface RulesRepository {

    suspend fun getContents(): Contents

    suspend fun getSection(sectionId: Long): Section?

    suspend fun getRule(ruleId: Long): Rule?

    suspend fun queryRulesByNumber(numbers: List<Int>): List<SearchResult>

    suspend fun queryRulesByContent(searchTerms: List<String>, limit: Int): List<SearchResult>
}