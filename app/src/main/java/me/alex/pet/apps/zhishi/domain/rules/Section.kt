package me.alex.pet.apps.zhishi.domain.rules

import me.alex.pet.apps.zhishi.domain.common.StyledText

data class Section(
        val id: Long,
        val name: StyledText,
        val rules: List<Rule>
)