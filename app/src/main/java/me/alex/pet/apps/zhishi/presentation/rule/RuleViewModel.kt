package me.alex.pet.apps.zhishi.presentation.rule

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.domain.StyledText

class RuleViewModel(
        private val ruleId: Long,
        private val rulesRepository: RulesRepository
) : ViewModel() {

    val viewState: LiveData<ViewState> = liveData {
        emit(ViewState((ruleId + 1).toInt(), StyledText("This is rule with ID $ruleId")))
    }
}