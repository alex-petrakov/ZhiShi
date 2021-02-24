package me.alex.pet.apps.zhishi.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.domain.contents.Contents
import me.alex.pet.apps.zhishi.domain.contents.ContentsChapter
import me.alex.pet.apps.zhishi.domain.contents.ContentsPart
import me.alex.pet.apps.zhishi.domain.contents.ContentsSection
import me.alex.pet.apps.zhishi.presentation.common.SingleLiveEvent

class HomeViewModel(rulesRepository: RulesRepository) : ViewModel() {

    val viewState: LiveData<ViewState> = liveData {
        val contents = rulesRepository.getContents()
        emit(contents.toViewState())
    }

    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    fun onClickSection(sectionId: Long) {
        _viewEffect.value = ViewEffect.NavigateToSection(sectionId)
    }
}

private fun Contents.toViewState(): ViewState {
    return ViewState(toUiModel())
}

private fun Contents.toUiModel(): List<ContentsElement> {
    return parts.flatMap { it.toUiModel() }
}

private fun ContentsPart.toUiModel(): List<ContentsElement> {
    return listOf(ContentsElement.Part(name)) + chapters.flatMap { it.toUiModel() }
}

private fun ContentsChapter.toUiModel(): List<ContentsElement> {
    return listOf(ContentsElement.Chapter(name)) + sections.map { it.toUiModel() }
}

private fun ContentsSection.toUiModel(): ContentsElement {
    return ContentsElement.Section(id, name)
}