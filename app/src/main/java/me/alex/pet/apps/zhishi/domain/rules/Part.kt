package me.alex.pet.apps.zhishi.domain.rules

data class Part(
        val id: Long,
        val name: String,
        val chapters: List<Chapter>
)