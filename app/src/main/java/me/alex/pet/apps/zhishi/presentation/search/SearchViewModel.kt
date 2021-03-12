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
import me.alex.pet.apps.zhishi.presentation.common.SingleLiveEvent

class SearchViewModel(private val searchRules: SearchRules) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>().apply {
        value = ViewState(SearchResults(false, emptyList()), EmptyView(false), SuggestionsView(true))
    }

    val viewState: LiveData<ViewState> get() = _viewState

    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    fun onUpdateQuery(query: String) {
        if (query.isEmpty()) {
            _viewState.value = ViewState(
                    SearchResults(false, emptyList()),
                    EmptyView(false),
                    SuggestionsView(true)
            )
            return
        }

        viewModelScope.launch {
            val results = searchRules(query)
            val uiModel = withContext(Dispatchers.Default) { results.map { it.toUiModel() } }
            _viewState.value = ViewState(
                    SearchResults(uiModel.isNotEmpty(), uiModel),
                    EmptyView(uiModel.isEmpty()),
                    SuggestionsView(false)
            )
        }
    }

    fun onClickRule(ruleId: Long) {
        _viewEffect.value = ViewEffect.NavigateToRule(ruleId)
    }
}

private fun SearchResult.toUiModel(): SearchResultItem {
    return SearchResultItem(ruleId, ruleNumber, snippet)
}
