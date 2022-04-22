package me.alex.pet.apps.zhishi.presentation.search

import androidx.lifecycle.*
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import me.alex.pet.apps.zhishi.domain.search.SearchRepository
import me.alex.pet.apps.zhishi.domain.search.SearchResult
import me.alex.pet.apps.zhishi.domain.search.SearchRules
import me.alex.pet.apps.zhishi.presentation.AppScreens
import me.alex.pet.apps.zhishi.presentation.common.SingleLiveEvent
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val searchRules: SearchRules,
    private val searchRepo: SearchRepository,
    private val router: Router
) : ViewModel() {

    private val query = MutableLiveData("")

    private val searchResults = Transformations.switchMap(query) { query ->
        liveData { emit(searchRules(query)) }
    }

    val viewState: LiveData<ViewState> =
        Transformations.switchMap(searchResults) { searchResults: List<SearchResult> ->
            liveData {
            val query = query.value!!
            val newState = ViewState(
                SearchResults(
                    query.isNotEmpty() && searchResults.isNotEmpty(),
                    searchResults.toUiModel()
                ),
                EmptyView(query.isNotEmpty() && searchResults.isEmpty()),
                SuggestionsView(query.isEmpty(), searchRepo.getSuggestions())
            )
                emit(newState)
            }
        }

    private val _viewEffect = SingleLiveEvent<ViewEffect>()
    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    private val isFirstStart: Boolean
        get() = savedStateHandle.keys().isEmpty()

    init {
        if (isFirstStart) {
            _viewEffect.value = ViewEffect.SHOW_KEYBOARD
            rememberFirstStart()
        }
    }

    fun onUpdateQuery(query: String) {
        this.query.value = query
    }

    fun onClickRule(ruleId: Long) {
        _viewEffect.value = ViewEffect.HIDE_KEYBOARD
        val ruleIds = searchResults.value!!.ruleIds
        val rulesToDisplay = RulesToDisplay(ruleIds, ruleIds.indexOf(ruleId))
        router.navigateTo(AppScreens.rules(rulesToDisplay, displaySectionButton = true))
    }

    fun onScrollResults() {
        _viewEffect.value = ViewEffect.HIDE_KEYBOARD
    }

    fun onBackPressed() {
        _viewEffect.value = ViewEffect.HIDE_KEYBOARD
        router.exit()
    }

    private fun rememberFirstStart() {
        savedStateHandle.set(STATE_FIRST_START_FLAG, false)
    }

    companion object {
        private const val STATE_FIRST_START_FLAG = "FIRST_START_FLAG"
    }
}

private val List<SearchResult>.ruleIds get() = map { it.ruleId }

private fun List<SearchResult>.toUiModel() = map { it.toUiModel() }

private fun SearchResult.toUiModel(): SearchResultItem {
    return SearchResultItem(ruleId, annotation, snippet)
}
