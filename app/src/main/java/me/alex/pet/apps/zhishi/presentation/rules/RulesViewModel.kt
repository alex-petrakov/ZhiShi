package me.alex.pet.apps.zhishi.presentation.rules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import me.alex.pet.apps.zhishi.presentation.common.extensions.getOrThrow
import javax.inject.Inject

@HiltViewModel
class RulesViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val router: Router
) : ViewModel() {

    private val rulesToDisplay: RulesToDisplay = stateHandle.getOrThrow(ARG_RULES_TO_DISPLAY)

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

    companion object {
        const val ARG_RULES_TO_DISPLAY = "RULES_TO_DISPLAY"
    }
}