package me.alex.pet.apps.zhishi.domain.search

import me.alex.pet.apps.zhishi.domain.StyledText

data class SearchResult(
        val ruleId: Long,
        val ruleNumber: Int,
        val snippet: StyledText
)