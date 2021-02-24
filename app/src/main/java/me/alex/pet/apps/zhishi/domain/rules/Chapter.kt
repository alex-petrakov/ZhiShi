package me.alex.pet.apps.zhishi.domain.rules

data class Chapter(
        val id: Long,
        val name: String,
        val sections: List<Section>
)