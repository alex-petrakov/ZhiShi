package me.alex.pet.apps.zhishi.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.domain.rules.Rule

class SearchViewModel(private val repository: RulesRepository) : ViewModel() {

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
            val searchTerms = withContext(Dispatchers.Default) {
                query.split("\\s".toRegex())
                        .filter { it.isNotBlank() }
            }
            val results = withContext(Dispatchers.IO) {
                repository.query(searchTerms, 30)
                        .map { it.toSearchResult() }
            }
            _viewState.value = _viewState.value!!.copy(searchResults = results)
        }
    }
}

private fun Rule.toSearchResult(): SearchResult {
    return SearchResult(id, number, content)
}