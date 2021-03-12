package me.alex.pet.apps.zhishi.presentation.search

import me.alex.pet.apps.zhishi.domain.StyledText

data class ViewState(
        val searchResults: SearchResults,
        val emptyView: EmptyView,
        val suggestionsView: SuggestionsView
)

data class EmptyView(
        val isVisible: Boolean
)

data class SuggestionsView(
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