package me.alex.pet.apps.zhishi.presentation.section

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.presentation.section.model.SectionViewState

class SectionViewModel(
        private val sectionId: Long,
        private val rulesRepository: RulesRepository
) : ViewModel() {

    val viewState: LiveData<SectionViewState> = liveData {
        emit(SectionViewState(sectionId.toInt()..sectionId.toInt()))
    }
}