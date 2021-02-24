package me.alex.pet.apps.zhishi.presentation.rule

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import me.alex.pet.apps.zhishi.domain.RulesRepository

class RuleViewModel(
        private val ruleId: Long,
        private val rulesRepository: RulesRepository
) : ViewModel() {

    val viewState: LiveData<ViewState> = liveData {
        val rule = rulesRepository.getRule(ruleId)
                ?: throw IllegalStateException("Rule with ID $ruleId was not found") // TODO: Consider showing an empty view instead
        emit(ViewState(rule.number, rule.content))
    }
}