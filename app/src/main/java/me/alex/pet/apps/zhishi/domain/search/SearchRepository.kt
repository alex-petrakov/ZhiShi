package me.alex.pet.apps.zhishi.domain.search

interface SearchRepository {

    fun getSuggestions(): List<String>

    suspend fun queryRulesById(numbers: List<Long>): List<SearchResult>

    suspend fun queryRulesByContent(searchTerms: List<String>, limit: Int): List<SearchResult>
}