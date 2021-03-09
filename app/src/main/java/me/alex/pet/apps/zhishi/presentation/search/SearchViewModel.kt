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
import me.alex.pet.apps.zhishi.domain.stemming.Stemmer
import java.util.*

class SearchViewModel(private val repository: RulesRepository) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>().apply {
        value = ViewState(emptyList())
    }

    val viewState: LiveData<ViewState> get() = _viewState

    private val stemmer = Stemmer()

    fun onUpdateQuery(query: String) {
        if (query.isEmpty()) {
            // TODO: Show search suggestions instead of showing empty list
            _viewState.value = _viewState.value!!.copy(searchResults = emptyList())
            return
        }

        viewModelScope.launch {
            val searchTerms = withContext(Dispatchers.Default) { prepareQuery(query) }
            val results = withContext(Dispatchers.IO) {
                repository.query(searchTerms, 30)
                        .map { it.toSearchResult() }
            }
            _viewState.value = _viewState.value!!.copy(searchResults = results)
        }
    }

    private fun prepareQuery(query: String): List<String> {
        return query.replace("ё", "е", true)
                .split("\\s".toRegex())
                .filter { it.isNotBlank() }
                .map { it.toLowerCase(Locale.getDefault()) }
                .map(stemmer::stem)
    }
}

private fun Rule.toSearchResult(): SearchResult {
    return SearchResult(id, number, content)
}
