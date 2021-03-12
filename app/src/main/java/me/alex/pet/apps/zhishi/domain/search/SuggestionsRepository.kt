package me.alex.pet.apps.zhishi.domain.search

interface SuggestionsRepository {

    fun getSuggestions(): List<String>
}