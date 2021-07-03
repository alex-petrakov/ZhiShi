package me.alex.pet.apps.zhishi.domain.search

data class SearchResult(
        val ruleId: Long,
        val annotation: String,
        val snippet: String
)