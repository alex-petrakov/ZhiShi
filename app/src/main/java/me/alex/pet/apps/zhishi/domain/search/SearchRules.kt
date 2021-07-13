package me.alex.pet.apps.zhishi.domain.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.search.stemming.Stemmer
import java.util.*

class SearchRules(
        private val repository: SearchRepository,
        private val stemmer: Stemmer
) {

    suspend operator fun invoke(query: String): List<SearchResult> {
        val searchTerms = withContext(Dispatchers.Default) {
            query.toLowerCase(Locale.getDefault())
                    .filter { it.isLetterOrDigit() || it.isWhitespace() }
                    .replace("ё", "е")
                    .split("\\s".toRegex())
                    .filter { it.isNotBlank() }
                    .map(stemmer::stem)
        }

        if (searchTerms.isEmpty()) {
            return emptyList()
        }

        val ruleIds = withContext(Dispatchers.Default) { searchTerms.mapNotNull { it.toLongOrNull() } }
        return repository.queryRulesById(ruleIds) + repository.queryRulesByContent(searchTerms, 15)
    }
}