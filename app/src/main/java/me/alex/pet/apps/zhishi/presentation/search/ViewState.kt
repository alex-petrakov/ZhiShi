package me.alex.pet.apps.zhishi.presentation.search

import androidx.recyclerview.widget.DiffUtil

data class ViewState(
        val searchResults: SearchResults,
        val emptyView: EmptyView,
        val suggestionsView: SuggestionsView
)

data class EmptyView(
        val isVisible: Boolean
)

data class SuggestionsView(
        val isVisible: Boolean,
        val suggestions: List<String>
)

data class SearchResults(
        val isVisible: Boolean,
        val items: List<SearchResultItem>
)

data class SearchResultItem(
        val ruleId: Long,
        val annotation: String,
        val snippet: String
) {
    object DiffCallback : DiffUtil.ItemCallback<SearchResultItem>() {
        override fun areItemsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
            return oldItem.ruleId == newItem.ruleId
        }

        override fun areContentsTheSame(oldItem: SearchResultItem, newItem: SearchResultItem): Boolean {
            return oldItem == newItem
        }
    }
}