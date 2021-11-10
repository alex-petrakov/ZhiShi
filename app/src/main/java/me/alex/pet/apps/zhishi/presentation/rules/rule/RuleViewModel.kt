package me.alex.pet.apps.zhishi.presentation.rules.rule

import androidx.lifecycle.*
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.presentation.AppScreens
import me.alex.pet.apps.zhishi.presentation.common.extensions.getOrThrow
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay
import javax.inject.Inject

@HiltViewModel
class RuleViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val rulesRepository: RulesRepository,
    private val router: Router
) : ViewModel() {

    private val ruleId: Long = stateHandle.getOrThrow(ARG_RULE_ID)

    private val displaySectionButton: Boolean = stateHandle.getOrThrow(ARG_DISPLAY_SECTION_BUTTON)

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

    companion object {
        const val ARG_RULE_ID = "RULE_ID"
        const val ARG_DISPLAY_SECTION_BUTTON = "DISPLAY_SECTION_BUTTON"
    }
}