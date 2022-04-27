package me.alex.pet.apps.zhishi.presentation.search

import androidx.lifecycle.*
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.search.SearchInteractor
import me.alex.pet.apps.zhishi.domain.search.SearchInteractor.InteractionResult.Failure
import me.alex.pet.apps.zhishi.domain.search.SearchInteractor.InteractionResult.Success
import me.alex.pet.apps.zhishi.domain.search.SearchResult
import me.alex.pet.apps.zhishi.presentation.AppScreens
import me.alex.pet.apps.zhishi.presentation.common.SingleLiveEvent
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val searchInteractor: SearchInteractor,
    private val router: Router
) : ViewModel() {

    private val _query = MutableLiveData(savedStateHandle.query)
    val query: LiveData<String> = Transformations.map(_query, Query::text)

    val viewState = Transformations.switchMap(_query) { query ->
        liveData(timeoutInMs = 0L) {
            // When the system restores screen state, don't apply debounce
            // and start search immediately
            if (query.source == Query.Source.USER) {
                delay(300L)
            }
            val searchResult = searchInteractor.searchRules(query.text)
            emit(searchResult.toViewState())
        }
    }

    private val _viewEffect = SingleLiveEvent<ViewEffect>()
    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    private val isFirstStart: Boolean
        get() = savedStateHandle[STATE_FIRST_START_FLAG] ?: true

    init {
        if (isFirstStart) {
            _viewEffect.value = ViewEffect.SHOW_KEYBOARD
            rememberFirstStart()
        }
    }

    fun onUpdateQuery(query: String) {
        if (query == _query.value!!.text) {
            return
        }
        val newQuery = Query(query, Query.Source.USER)
        _query.value = newQuery
        savedStateHandle.query = newQuery
    }

    fun onClickRule(ruleId: Long) {
        val currentViewState = viewState.value as? ViewState.Content ?: return
        _viewEffect.value = ViewEffect.HIDE_KEYBOARD
        val ruleIds = currentViewState.searchResults.ruleIds
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

private val List<SearchResultUiModel>.ruleIds get() = map { it.ruleId }

private suspend fun SearchInteractor.InteractionResult.toViewState(): ViewState {
    return withContext(Dispatchers.Default) {
        when (this@toViewState) {
            is Failure -> ViewState.Suggestions(suggestedQueries)
            is Success -> searchResults.toViewState()
        }
    }
}

private fun List<SearchResult>.toViewState(): ViewState {
    return if (this.isEmpty()) {
        ViewState.Empty
    } else {
        return ViewState.Content(this.toUiModel())
    }
}

private fun List<SearchResult>.toUiModel() = map { it.toUiModel() }

private fun SearchResult.toUiModel(): SearchResultUiModel {
    return SearchResultUiModel(ruleId, annotation, snippet)
}
