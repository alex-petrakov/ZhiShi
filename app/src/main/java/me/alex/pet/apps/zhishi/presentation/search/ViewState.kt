package me.alex.pet.apps.zhishi.presentation.search

import androidx.recyclerview.widget.DiffUtil

sealed class ViewState {
    object Empty : ViewState()
    data class Suggestions(val suggestions: List<String>) : ViewState()
    data class Content(val searchResults: List<SearchResultUiModel>) : ViewState()
}

data class SearchResultUiModel(
    val ruleId: Long,
    val annotation: String,
    val snippet: String
) {
    object DiffCallback : DiffUtil.ItemCallback<SearchResultUiModel>() {
        override fun areItemsTheSame(
            oldItem: SearchResultUiModel,
            newItem: SearchResultUiModel
        ): Boolean {
            return oldItem.ruleId == newItem.ruleId
        }

        override fun areContentsTheSame(
            oldItem: SearchResultUiModel,
            newItem: SearchResultUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}