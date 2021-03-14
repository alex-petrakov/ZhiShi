package me.alex.pet.apps.zhishi.domain.search.stemming

import me.alex.pet.apps.zhishi.domain.search.stemming.snowball.RussianStemmer

class Stemmer {
    private val stemmer = RussianStemmer()

    fun stem(string: String): String {
        stemmer.current = string
        stemmer.stem()
        return stemmer.current
    }
}