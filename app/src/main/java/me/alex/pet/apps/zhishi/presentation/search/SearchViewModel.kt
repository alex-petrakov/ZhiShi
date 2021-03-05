package me.alex.pet.apps.zhishi.presentation.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.alex.pet.apps.zhishi.domain.StyledText
import kotlin.random.Random

class SearchViewModel : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>().apply {
        value = ViewState(emptyList())
    }

    val viewState: LiveData<ViewState> get() = _viewState

    private val random = Random(100)

    fun onUpdateQuery(query: String) {
        val results = (1..10).map { // TODO: Fetch real results from the repository
            val number = random.nextInt(0, 6)
            val id = (number - 1).toLong()
            SearchResult(id, number, StyledText(query))
        }
        _viewState.value = _viewState.value!!.copy(searchResults = results)
    }
}