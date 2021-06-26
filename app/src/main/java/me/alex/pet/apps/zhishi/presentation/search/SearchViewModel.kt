package me.alex.pet.apps.zhishi.presentation.search

import androidx.lifecycle.*
import com.github.terrakok.cicerone.Router
import me.alex.pet.apps.zhishi.domain.search.SearchRepository
import me.alex.pet.apps.zhishi.domain.search.SearchResult
import me.alex.pet.apps.zhishi.domain.search.SearchRules
import me.alex.pet.apps.zhishi.presentation.AppScreens
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay

class SearchViewModel(
        private val searchRules: SearchRules,
        private val searchRepo: SearchRepository,
        private val router: Router
) : ViewModel() {

    private val query = MutableLiveData<String>().apply {
        value = ""
    }

    private val searchResults = Transformations.switchMap(query) { query ->
        liveData { emit(searchRules(query)) }
    }

    val viewState: LiveData<ViewState> = Transformations.switchMap(searchResults) { searchResults: List<SearchResult> ->
        liveData {
            val query = query.value!!
            val newState = ViewState(
                    SearchResults(query.isNotEmpty() && searchResults.isNotEmpty(), searchResults.toUiModel()),
                    EmptyView(query.isNotEmpty() && searchResults.isEmpty()),
                    SuggestionsView(query.isEmpty(), searchRepo.getSuggestions())
            )
            emit(newState)
        }
    }

    fun onUpdateQuery(query: String) {
        this.query.value = query
    }

    fun onClickRule(ruleId: Long) {
        val ruleIds = searchResults.value!!.ruleIds
        val rulesToDisplay = RulesToDisplay(ruleIds, ruleIds.indexOf(ruleId))
        router.navigateTo(AppScreens.rules(rulesToDisplay))
    }
}

private val List<SearchResult>.ruleIds get() = map { it.ruleId }

private fun List<SearchResult>.toUiModel() = map { it.toUiModel() }

private fun SearchResult.toUiModel(): SearchResultItem {
    return SearchResultItem(ruleId, snippet)
}
