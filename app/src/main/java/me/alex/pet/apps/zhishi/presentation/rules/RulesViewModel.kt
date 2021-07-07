package me.alex.pet.apps.zhishi.presentation.rules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router

class RulesViewModel(
        private val rulesToDisplay: RulesToDisplay,
        private val router: Router
) : ViewModel() {

    private val _viewState = MutableLiveData(
            ViewState(rulesToDisplay.ids[rulesToDisplay.selectionIndex])
    )

    val viewState: LiveData<ViewState> get() = _viewState

    fun onRuleSelected(position: Int) {
        _viewState.value = ViewState(rulesToDisplay.ids[position])
    }

    fun onBackPressed() {
        router.exit()
    }
}