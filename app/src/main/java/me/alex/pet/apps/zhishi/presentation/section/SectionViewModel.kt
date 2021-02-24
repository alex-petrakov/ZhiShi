package me.alex.pet.apps.zhishi.presentation.section

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.domain.rules.Rule
import me.alex.pet.apps.zhishi.domain.rules.Section
import me.alex.pet.apps.zhishi.presentation.section.model.DisplayableElement
import me.alex.pet.apps.zhishi.presentation.section.model.SectionViewState

class SectionViewModel(
        private val sectionId: Long,
        private val rulesRepository: RulesRepository
) : ViewModel() {

    val viewState: LiveData<SectionViewState> = liveData {
        val section = rulesRepository.getSection(sectionId)
                ?: throw IllegalStateException("Section with ID $sectionId was not found") // TODO: Consider showing an empty view instead
        val ruleNumbersRange = section.ruleNumbersRange()
        val displayableElements = section.toUiModel()
        emit(SectionViewState(ruleNumbersRange, displayableElements))
    }
}

private fun Section.ruleNumbersRange(): IntRange {
    val first = this.rules.first().number
    val last = this.rules.last().number
    return first..last
}

private fun Section.toUiModel(): List<DisplayableElement> {
    val heading = listOf(DisplayableElement.Heading(this.name))
    return heading + rules.map { it.toUiModel() }
}

private fun Rule.toUiModel(): DisplayableElement {
    return DisplayableElement.Rule(
            this.id,
            this.number,
            this.content
    )
}