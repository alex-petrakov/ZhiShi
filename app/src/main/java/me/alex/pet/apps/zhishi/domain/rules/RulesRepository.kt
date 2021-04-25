package me.alex.pet.apps.zhishi.domain.rules

import me.alex.pet.apps.zhishi.domain.search.SearchResult

interface RulesRepository {

    suspend fun getSection(sectionId: Long): Section?

    suspend fun getRule(ruleId: Long): Rule?

    suspend fun queryRulesById(numbers: List<Long>): List<SearchResult>

    suspend fun queryRulesByContent(searchTerms: List<String>, limit: Int): List<SearchResult>
}