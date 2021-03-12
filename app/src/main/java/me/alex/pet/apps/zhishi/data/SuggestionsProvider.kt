package me.alex.pet.apps.zhishi.data

import me.alex.pet.apps.zhishi.domain.search.SuggestionsRepository

class SuggestionsProvider : SuggestionsRepository {

    private val suggestions = listOf(
            "140",
            "двойное н",
            "пре при",
            "не ни",
            "гар гор",
            "союзы",
            "обращения",
            "правила переноса",
            "тире",
            "прямая речь"
    )

    override fun getSuggestions(): List<String> {
        return suggestions
    }
}