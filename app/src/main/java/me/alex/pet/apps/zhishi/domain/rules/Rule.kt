package me.alex.pet.apps.zhishi.domain.rules

import me.alex.pet.apps.zhishi.domain.StyledText

data class Rule(
        val id: Long,
        val number: Int,
        val annotation: StyledText,
        val content: StyledText
)