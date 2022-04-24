package me.alex.pet.apps.zhishi.presentation.search

import androidx.lifecycle.*
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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

    private val _query = MutableLiveData(savedStateHandle.query)
    val query: LiveData<String> = Transformations.map(_query, Query::text)

    private val searchResults = Transformations.switchMap(_query) { query ->
        liveData(timeoutInMs = 0L) {
            // When the system restores screen state, don't apply debounce
            // and start search immediately
            if (query.source == Query.Source.USER) {
                delay(300L)
            }
            emit(searchRules(query.text))
        }
    }

    val viewState: LiveData<ViewState> = Transformations.switchMap(searchResults) { searchResults ->
        liveData {
            val currentQuery = query.value!!
            val newState = when {
                currentQuery.isEmpty() -> ViewState.Suggestions(searchRepo.getSuggestions())
                searchResults.isEmpty() -> ViewState.Empty
                else -> ViewState.Content(searchResults.toUiModel())
            }
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
        val newQuery = Query(query, Query.Source.USER)
        _query.value = newQuery
        savedStateHandle.query = newQuery
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

    private var SavedStateHandle.query: Query
        get() = Query(get(STATE_QUERY) ?: "", Query.Source.SYSTEM)
        set(value) = set(STATE_QUERY, value.text)

    private data class Query(val text: String, val source: Source) {
        enum class Source { SYSTEM, USER }
    }

    companion object {
        private const val STATE_FIRST_START_FLAG = "FIRST_START_FLAG"
        private const val STATE_QUERY = "STATE_QUERY"
    }
}

private val List<SearchResult>.ruleIds get() = map { it.ruleId }

private fun List<SearchResult>.toUiModel() = map { it.toUiModel() }

private fun SearchResult.toUiModel(): SearchResultItem {
    return SearchResultItem(ruleId, annotation, snippet)
}
