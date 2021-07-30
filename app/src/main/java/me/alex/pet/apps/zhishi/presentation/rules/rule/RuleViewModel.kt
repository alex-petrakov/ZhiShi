package me.alex.pet.apps.zhishi.presentation.rules.rule

import androidx.lifecycle.*
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.presentation.AppScreens
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay

class RuleViewModel(
        private val ruleId: Long,
        private val displaySectionButton: Boolean,
        private val rulesRepository: RulesRepository,
        private val router: Router
) : ViewModel() {

    private val rule: LiveData<Rule> = liveData {
        val rule = rulesRepository.getRule(ruleId)
                ?: throw IllegalStateException("Rule with ID $ruleId was not found")
        emit(rule)
    }

    val viewState: LiveData<ViewState> = Transformations.switchMap(rule) { rule ->
        liveData { emit(mapRuleToViewState(rule)) }
    }

    fun onRuleLinkClick(ruleId: Long) {
        viewModelScope.launch {
            val neighbours = rulesRepository.getIdsOfRulesInSameSection(ruleId)
            val selectionIndex = neighbours.indexOf(ruleId)
            val rulesToDisplay = RulesToDisplay(neighbours.toList(), selectionIndex)
            router.navigateTo(AppScreens.rules(rulesToDisplay))
        }
    }

    private suspend fun mapRuleToViewState(rule: Rule): ViewState {
        return withContext(Dispatchers.Default) {
            ViewState(rule.content, displaySectionButton, rule.section.name)
        }
    }

    fun onNavigateToSection() {
        router.navigateTo(AppScreens.section(rule.value!!.section.id))
    }
}