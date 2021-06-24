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
        val newState = mapContentsToViewState(contents)
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

    private suspend fun mapContentsToViewState(contents: Contents): ViewState {
        return withContext(Dispatchers.Default) {
            ViewState(contents.toUiModel())
        }
    }
}

private fun Contents.toUiModel(): List<DisplayableItem> {
    return parts.flatMap { it.toUiModel() }
}

private fun PartNode.toUiModel(): List<DisplayableItem> {
    return listOf(DisplayableItem.Part(name)) + chapters.flatMap { it.toUiModel() }
}

private fun ChapterNode.toUiModel(): List<DisplayableItem> {
    return listOf(DisplayableItem.Chapter(name)) + sections.map { it.toUiModel() }
}

private fun SectionNode.toUiModel(): DisplayableItem {
    return DisplayableItem.Section(id, name)
}