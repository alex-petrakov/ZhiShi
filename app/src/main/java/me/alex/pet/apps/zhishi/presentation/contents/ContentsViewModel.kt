package me.alex.pet.apps.zhishi.presentation.contents

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.terrakok.cicerone.Router
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.contents.*
import me.alex.pet.apps.zhishi.presentation.AppScreens

class ContentsViewModel(
        contentsRepository: ContentsRepository,
        private val router: Router
) : ViewModel() {

    val viewState: LiveData<ViewState> = liveData {
        val contents = contentsRepository.getContents()
        val newState = mapContentsToViewState(contents)
        emit(newState)
    }

    fun onClickSection(sectionId: Long) {
        router.navigateTo(AppScreens.section(sectionId))
    }

    fun onClickSearchAction() {
        router.navigateTo(AppScreens.search())
    }

    fun onClickAboutAction() {
        router.navigateTo(AppScreens.about())
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