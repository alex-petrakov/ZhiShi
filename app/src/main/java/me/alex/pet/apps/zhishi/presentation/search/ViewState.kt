package me.alex.pet.apps.zhishi.presentation.search

import me.alex.pet.apps.zhishi.domain.StyledText

data class ViewState(
        val searchResults: List<SearchResultItem>
)

data class SearchResultItem(
        val ruleId: Long,
        val ruleNumber: Int,
        val snippet: StyledText
)