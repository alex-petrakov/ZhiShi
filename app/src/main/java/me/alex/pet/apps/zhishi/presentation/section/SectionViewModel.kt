package me.alex.pet.apps.zhishi.presentation.section

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.sections.Rule
import me.alex.pet.apps.zhishi.domain.sections.Section
import me.alex.pet.apps.zhishi.domain.sections.SectionsRepository
import me.alex.pet.apps.zhishi.presentation.AppScreens
import me.alex.pet.apps.zhishi.presentation.common.extensions.getOrThrow
import me.alex.pet.apps.zhishi.presentation.rules.RulesToDisplay
import javax.inject.Inject

@HiltViewModel
class SectionViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val sectionsRepository: SectionsRepository,
    private val router: Router
) : ViewModel() {

    private val sectionId: Long = stateHandle.getOrThrow(ARG_SECTION_ID)

    private val section = liveData {
        val section = sectionsRepository.getSection(sectionId)
            ?: throw IllegalStateException("Section with ID $sectionId was not found")
        emit(section)
    }

    val viewState = section.switchMap { section ->
        liveData { emit(mapSectionToViewState(section)) }
    }

    fun onClickRule(ruleId: Long) {
        val ruleIds = section.value!!.ruleIds
        val rulesToDisplay = RulesToDisplay(ruleIds.toList(), ruleIds.indexOf(ruleId))
        router.navigateTo(AppScreens.rules(rulesToDisplay))
    }

    fun onBackPressed() {
        router.exit()
    }

    private suspend fun mapSectionToViewState(section: Section): ViewState {
        return withContext(Dispatchers.Default) {
            ViewState(section.ruleIds, section.toUiModel())
        }
    }

    companion object {
        const val ARG_SECTION_ID = "SECTION_ID"
    }
}

private fun Section.toUiModel(): List<DisplayableItem> {
    val heading = listOf(DisplayableItem.Heading(this.name))
    return heading + rules.map { it.toUiModel() }
}

private fun Rule.toUiModel(): DisplayableItem {
    return DisplayableItem.Rule(
        this.id,
        this.annotation
    )
}
