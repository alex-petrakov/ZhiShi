package me.alex.pet.apps.zhishi.presentation.rules

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RulesViewModel(private val rulesToDisplay: RulesToDisplay) : ViewModel() {

    private val _viewState = MutableLiveData<ViewState>().apply {
        // TODO: load and show rule number instead of rule ID
        value = ViewState(rulesToDisplay.ids[rulesToDisplay.selectionIndex].toInt())
    }

    val viewState: LiveData<ViewState> get() = _viewState

    fun onRuleSelected(position: Int) {
        // TODO: load and show rule number instead of rule ID
        _viewState.value = ViewState(rulesToDisplay.ids[position].toInt())
    }
}

data class ViewState(val selectedRuleNumber: Int)