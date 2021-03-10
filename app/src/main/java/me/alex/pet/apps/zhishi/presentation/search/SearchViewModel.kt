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
        value = ViewState(EmptyView(false), SearchResults(true, emptyList()))
    }

    val viewState: LiveData<ViewState> get() = _viewState

    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    fun onUpdateQuery(query: String) {
        if (query.isEmpty()) {
            // TODO: Show search suggestions instead of showing empty list
            _viewState.value = ViewState(
                    EmptyView(false),
                    SearchResults(true, emptyList())
            )
            return
        }

        viewModelScope.launch {
            val results = searchRules(query)
            val uiModel = withContext(Dispatchers.Default) { results.map { it.toUiModel() } }
            _viewState.value = ViewState(
                    EmptyView(uiModel.isEmpty()),
                    SearchResults(uiModel.isNotEmpty(), uiModel)
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
