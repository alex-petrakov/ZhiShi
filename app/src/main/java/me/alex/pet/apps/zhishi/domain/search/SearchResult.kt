package me.alex.pet.apps.zhishi.domain.search

import me.alex.pet.apps.zhishi.domain.common.StyledText

data class SearchResult(
        val ruleId: Long,
        val snippet: StyledText
)