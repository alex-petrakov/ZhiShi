package me.alex.pet.apps.zhishi.domain.rules

import me.alex.pet.apps.zhishi.domain.common.StyledText

data class Rule(
        val id: Long,
        val number: Int,
        val annotation: StyledText,
        val content: StyledText
)