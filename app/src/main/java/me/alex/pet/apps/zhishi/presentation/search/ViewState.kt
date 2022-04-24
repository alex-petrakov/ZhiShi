package me.alex.pet.apps.zhishi.presentation.search

import androidx.recyclerview.widget.DiffUtil

sealed class ViewState {
    object Empty : ViewState()
    data class Suggestions(val suggestions: List<String>) : ViewState()
    data class Content(val searchResults: List<SearchResultItem>) : ViewState()
}

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