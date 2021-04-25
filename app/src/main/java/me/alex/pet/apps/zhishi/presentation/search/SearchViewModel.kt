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
import me.alex.pet.apps.zhishi.domain.search.SuggestionsRepository
import me.alex.pet.apps.zhishi.presentation.common.SingleLiveEvent

class SearchViewModel(
        private val searchRules: SearchRules,
        private val suggestionsRepo: SuggestionsRepository
) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>().apply {
        val searchSuggestions = suggestionsRepo.getSuggestions()
        val viewState = ViewState(
                SearchResults(false, emptyList()),
                EmptyView(false),
                SuggestionsView(true, searchSuggestions)
        )
        value = viewState
    }

    val viewState: LiveData<ViewState> get() = _viewState

    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    fun onUpdateQuery(query: String) {
        if (query.isEmpty()) {
            _viewState.value = ViewState(
                    SearchResults(false, emptyList()),
                    EmptyView(false),
                    _viewState.value!!.suggestionsView.copy(isVisible = true)
            )
            return
        }

        viewModelScope.launch {
            val results = searchRules(query)
            val uiModel = withContext(Dispatchers.Default) { results.map { it.toUiModel() } }
            _viewState.value = ViewState(
                    SearchResults(uiModel.isNotEmpty(), uiModel),
                    EmptyView(uiModel.isEmpty()),
                    _viewState.value!!.suggestionsView.copy(isVisible = false)
            )
        }
    }

    fun onClickRule(ruleId: Long) {
        _viewEffect.value = ViewEffect.NavigateToRule(ruleId)
    }
}

private fun SearchResult.toUiModel(): SearchResultItem {
    return SearchResultItem(ruleId, snippet)
}
