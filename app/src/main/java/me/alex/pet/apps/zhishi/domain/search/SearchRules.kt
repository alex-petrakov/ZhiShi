package me.alex.pet.apps.zhishi.domain.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.RulesRepository
import me.alex.pet.apps.zhishi.domain.stemming.Stemmer
import java.util.*

class SearchRules(
        private val repository: RulesRepository,
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

        return repository.query(searchTerms, 30)
    }
}