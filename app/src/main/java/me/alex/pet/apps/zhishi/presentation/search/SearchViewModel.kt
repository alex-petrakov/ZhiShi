package me.alex.pet.apps.zhishi.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.search.SearchResult
import me.alex.pet.apps.zhishi.domain.search.SearchRules

class SearchViewModel(private val searchRules: SearchRules) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>().apply {
        value = ViewState(emptyList())
    }

    val viewState: LiveData<ViewState> get() = _viewState

    fun onUpdateQuery(query: String) {
        if (query.isEmpty()) {
            // TODO: Show search suggestions instead of showing empty list
            _viewState.value = _viewState.value!!.copy(searchResults = emptyList())
            return
        }

        viewModelScope.launch {
            val results = searchRules(query)
            val uiModel = withContext(Dispatchers.Default) { results.map { it.toUiModel() } }
            _viewState.value = _viewState.value!!.copy(searchResults = uiModel)
        }
    }
}

private fun SearchResult.toUiModel(): SearchResultItem {
    return SearchResultItem(ruleId, ruleNumber, snippet)
}
