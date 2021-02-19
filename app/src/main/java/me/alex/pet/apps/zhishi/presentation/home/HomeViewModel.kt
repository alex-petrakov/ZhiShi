package me.alex.pet.apps.zhishi.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.alex.pet.apps.zhishi.domain.StyledText
import me.alex.pet.apps.zhishi.presentation.home.model.ContentsElement
import me.alex.pet.apps.zhishi.presentation.home.model.HomeViewState

class HomeViewModel : ViewModel() {

    private val _viewState = MutableLiveData<HomeViewState>()

    val viewState: LiveData<HomeViewState> get() = _viewState

    init {
        _viewState.value = HomeViewState(
                listOf(
                        ContentsElement.Part("Орфография"),
                        ContentsElement.Chapter("Правописание гласных"),
                        ContentsElement.Section(1, StyledText("Гласные после шипящих и ц")),
                        ContentsElement.Section(2, StyledText("Гласные ы и и после приставок")),
                        ContentsElement.Section(3, StyledText("Буква э")),
                        ContentsElement.Section(4, StyledText("Буква ё")),
                        ContentsElement.Section(5, StyledText("Общие правила правописание неударяемых гласных")),
                        ContentsElement.Section(6, StyledText("Неударяемые гласные в корнях слов")),
                        ContentsElement.Section(7, StyledText("Неударяемые гласные в приставках")),
                        ContentsElement.Section(8, StyledText("Неударяемые гласные в суффиксах")),
                        ContentsElement.Section(9, StyledText("Неударяемые соединительные глассные")),
                        ContentsElement.Section(10, StyledText("Гласные в некоторых неударяемых падежных окончаниях")),
                        ContentsElement.Section(11, StyledText("Неударяемые гласные в личных глагольных окончаниях")),
                        ContentsElement.Chapter("Правописание согласных"),
                        ContentsElement.Section(12, StyledText("Общие правила")),
                        ContentsElement.Section(13, StyledText("Двойные согласные"))
                )
        )
    }
}