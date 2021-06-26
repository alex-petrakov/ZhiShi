package me.alex.pet.apps.zhishi.domain.sections

import me.alex.pet.apps.zhishi.domain.common.StyledText

data class Section(
        val id: Long,
        val name: StyledText,
        val rules: List<Rule>
) {
    val ruleIds get() = rules.first().id..rules.last().id
}