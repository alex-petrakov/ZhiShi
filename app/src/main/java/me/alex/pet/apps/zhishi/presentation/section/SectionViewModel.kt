package me.alex.pet.apps.zhishi.presentation.section

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.RulesRepository
import me.alex.pet.apps.zhishi.domain.rules.Section
import me.alex.pet.apps.zhishi.presentation.common.SingleLiveEvent
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay

class SectionViewModel(
        private val sectionId: Long,
        private val rulesRepository: RulesRepository
) : ViewModel() {

    private val section = liveData {
        val section = rulesRepository.getSection(sectionId)
                ?: throw IllegalStateException("Section with ID $sectionId was not found") // TODO: consider displaying an error message
        emit(section)
    }

    val viewState = Transformations.switchMap(section) { section ->
        liveData { emit(section.toViewState()) }
    }

    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    fun onClickRule(ruleId: Long) {
        val ruleIds = section.value!!.ruleIds
        check(ruleId in ruleIds)
        _viewEffect.value = ViewEffect.NavigateToRule(RulesToDisplay(ruleIds, ruleIds.indexOf(ruleId)))
    }
}

private val Section.ruleNumbersRange: IntRange
    get() {
        val first = this.rules.first().number
        val last = this.rules.last().number
        return first..last
    }

private val Section.ruleIds get() = rules.map { it.id }

private fun Section.toViewState(): ViewState {
    return ViewState(this.ruleNumbersRange, this.toUiModel())
}

private fun Section.toUiModel(): List<DisplayableElement> {
    val heading = listOf(DisplayableElement.Heading(this.name))
    return heading + rules.map { it.toUiModel() }
}

private fun Rule.toUiModel(): DisplayableElement {
    return DisplayableElement.Rule(
            this.id,
            this.number,
            this.annotation
    )
}