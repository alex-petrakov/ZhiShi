package me.alex.pet.apps.zhishi.presentation.rules.rule

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.presentation.common.SingleLiveEvent
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay

class RuleViewModel(
        private val ruleId: Long,
        private val rulesRepository: RulesRepository
) : ViewModel() {

    val viewState: LiveData<ViewState> = liveData {
        val rule = rulesRepository.getRule(ruleId)
                ?: throw IllegalStateException("Rule with ID $ruleId was not found") // TODO: Consider showing an empty view instead
        emit(ViewState(rule.content))
    }

    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    fun onRuleLinkClick(ruleId: Long) {
        viewModelScope.launch {
            val neighbours = rulesRepository.getIdsOfRulesInSameSection(ruleId)
            val selectionIndex = neighbours.indexOf(ruleId)
            _viewEffect.value = ViewEffect.NavigateToRule(
                    RulesToDisplay(neighbours.toList(), selectionIndex)
            )
        }
    }
}