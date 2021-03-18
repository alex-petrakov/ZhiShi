package me.alex.pet.apps.zhishi.presentation.contents

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.contents.*
import me.alex.pet.apps.zhishi.presentation.common.SingleLiveEvent

class ContentsViewModel(contentsRepository: ContentsRepository) : ViewModel() {

    val viewState: LiveData<ViewState> = liveData {
        val contents = contentsRepository.getContents()
        val newState = withContext(Dispatchers.Default) { contents.toViewState() }
        emit(newState)
    }

    private val _viewEffect = SingleLiveEvent<ViewEffect>()

    val viewEffect: LiveData<ViewEffect> get() = _viewEffect

    fun onClickSection(sectionId: Long) {
        _viewEffect.value = ViewEffect.NavigateToSection(sectionId)
    }

    fun onClickSearchAction() {
        _viewEffect.value = ViewEffect.NavigateToSearch
    }
}

private fun Contents.toViewState(): ViewState {
    return ViewState(toUiModel())
}

private fun Contents.toUiModel(): List<ContentsElement> {
    return parts.flatMap { it.toUiModel() }
}

private fun PartNode.toUiModel(): List<ContentsElement> {
    return listOf(ContentsElement.Part(name)) + chapters.flatMap { it.toUiModel() }
}

private fun ChapterNode.toUiModel(): List<ContentsElement> {
    return listOf(ContentsElement.Chapter(name)) + sections.map { it.toUiModel() }
}

private fun SectionNode.toUiModel(): ContentsElement {
    return ContentsElement.Section(id, name)
}