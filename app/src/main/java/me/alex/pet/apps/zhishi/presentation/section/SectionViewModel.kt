package me.alex.pet.apps.zhishi.presentation.section

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import me.alex.pet.apps.zhishi.domain.CharacterStyle
import me.alex.pet.apps.zhishi.domain.CharacterStyleType
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.domain.StyledText
import me.alex.pet.apps.zhishi.presentation.section.model.DisplayableElement
import me.alex.pet.apps.zhishi.presentation.section.model.SectionViewState

class SectionViewModel(
        private val sectionId: Long,
        private val rulesRepository: RulesRepository
) : ViewModel() {

    val viewState: LiveData<SectionViewState> = liveData {
        val elements = getElements()
        val s = (elements[1] as DisplayableElement.Rule).number
        val e = (elements.last() as DisplayableElement.Rule).number
        emit(SectionViewState(s..e, getElements()))
    }

    private fun getElements(): List<DisplayableElement> {
        return listOf(
                DisplayableElement.Heading(
                        StyledText(
                                "Гласные после шипящих и ц",
                                characterStyles = listOf(
                                        CharacterStyle(24, 25, CharacterStyleType.EMPHASIS)
                                )
                        )
                ),
                DisplayableElement.Rule(
                        0,
                        1,
                        StyledText(
                                "После ж, ч, ш, щ не пишутся ю, я, ы, а пишутся у, а, и, например: чудо, щука, час, роща, жир, шить.\n\nБуквы ю и я допускаются после этих согласных только в иноязычных словах (преимущественно французских), например: жюри, парашют (в том числе — в именах собственных, например: Сен-Жюст), а также в сложносокращённых словах и буквенных аббревиатурах, в которых, по общему правилу, допускаются любые сочетания букв (см. § 110)."
                        )
                ),
                DisplayableElement.Rule(
                        1,
                        2,
                        StyledText(
                                "После ц буква ы пишется в окончаниях и в суффиксе -ын, например: птицы, о́вцы и овцы́, огурцы, белолицый, сестрицын, лисицын, а также в словах цыган, цыплёнок, на цыпочках, цыц (междометие) и в других словах того же корня.\n\nВ остальных случаях после ц пишется всегда и, например: станция, цибик, циновка, цимбалы, цинк, медицина. "
                        )
                ),
                DisplayableElement.Rule(
                        2,
                        3,
                        StyledText(
                                "После ц буквы ю и я допускаются только в иноязычных именах собственных, например Цюрих, Свенцяны."
                        )
                )
        )
    }
}