package me.alex.pet.apps.zhishi.presentation.search

import me.alex.pet.apps.zhishi.domain.StyledText

data class ViewState(
        val emptyView: EmptyView,
        val searchResults: SearchResults
)

data class EmptyView(
        val isVisible: Boolean
)

data class SearchResults(
        val isVisible: Boolean,
        val items: List<SearchResultItem>
)

data class SearchResultItem(
        val ruleId: Long,
        val ruleNumber: Int,
        val snippet: StyledText
)