package me.alex.pet.apps.zhishi.domain.search

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alex.pet.apps.zhishi.domain.search.stemming.Stemmer
import java.util.*
import javax.inject.Inject

class SearchInteractor @Inject constructor(
    private val repository: SearchRepository,
    private val stemmer: Stemmer
) {

    suspend fun searchRules(query: String): InteractionResult {
        if (query.isEmpty()) {
            return InteractionResult.Failure(getSearchSuggestions())
        }

        val searchTerms = withContext(Dispatchers.Default) {
            extractSearchTermsFrom(query)
        }
        if (searchTerms.isEmpty()) {
            return InteractionResult.Success(emptyList())
        }

        val ruleIds = withContext(Dispatchers.Default) {
            searchTerms.mapNotNull { it.toLongOrNull() }
        }

        val searchResults = repository.queryRulesById(ruleIds) +
                repository.queryRulesByContent(searchTerms, 15)
        return InteractionResult.Success(searchResults)
    }

    private fun extractSearchTermsFrom(query: String): List<String> {
        return query.lowercase(Locale.getDefault())
            .filter { it.isLetterOrDigit() || it.isWhitespace() }
            .replace("ё", "е")
            .split("\\s".toRegex())
            .filter { it.isNotBlank() }
            .map(stemmer::stem)
    }

    private fun getSearchSuggestions(): List<String> {
        return repository.getSuggestions()
    }

    sealed class InteractionResult {
        data class Failure(val suggestedQueries: List<String>) : InteractionResult()
        data class Success(val searchResults: List<SearchResult>) : InteractionResult()
    }
}