package me.alex.pet.apps.zhishi.presentation.search

import androidx.lifecycle.*
import me.alex.pet.apps.zhishi.domain.search.SearchResult
import me.alex.pet.apps.zhishi.domain.search.SearchRules
import me.alex.pet.apps.zhishi.domain.search.SuggestionsRepository
import me.alex.pet.apps.zhishi.presentation.common.SingleLiveEvent
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay

class SearchViewModel(
        private val searchRules: SearchRules,
        private val suggestionsRepo: SuggestionsRepository
) : ViewModel() {

    private val query = MutableLiveData<String>().apply {
        value = ""
    }

    private val searchResults = Transformations.switchMap(query) { query ->
        liveData { emit(searchRules(query)) }
    }

    val viewState: LiveData<ViewState> = Transformations.switchMap(searchResults) { searchResults: List<SearchResult> ->
        liveData {
            val newState = ViewState(
                    SearchResults(searchResults.isNotEmpty(), searchResults.toUiModel()),
                    EmptyView(searchResults.isEmpty() && !query.value.isNullOrEmpty()),
                    SuggestionsView(searchResults.isEmpty() && query.value.isNullOrEmpty(), suggestionsRepo.getSuggestions())
            )
            emit(newState)
        }
    }

    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    fun onUpdateQuery(query: String) {
        this.query.value = query
    }

    fun onClickRule(ruleId: Long) {
        val ruleIds = searchResults.value!!.ruleIds
        check(ruleId in ruleIds)
        _viewEffect.value = ViewEffect.NavigateToRule(RulesToDisplay(ruleIds, ruleIds.indexOf(ruleId)))
    }
}

private val List<SearchResult>.ruleIds get() = map { it.ruleId }

private fun List<SearchResult>.toUiModel() = map { it.toUiModel() }

private fun SearchResult.toUiModel(): SearchResultItem {
    return SearchResultItem(ruleId, snippet)
}
