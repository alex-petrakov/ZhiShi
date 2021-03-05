package me.alex.pet.apps.zhishi.presentation.search

import me.alex.pet.apps.zhishi.domain.StyledText

data class ViewState(
        val searchResults: List<SearchResult>
)

data class SearchResult(
        val ruleId: Long,
        val ruleNumber: Int,
        val snippet: StyledText
)