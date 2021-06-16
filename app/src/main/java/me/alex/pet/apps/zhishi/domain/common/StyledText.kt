package me.alex.pet.apps.zhishi.domain.common

data class StyledText(
        val string: String,
        val paragraphSpans: List<ParagraphSpan> = emptyList(),
        val characterSpans: List<CharacterSpan> = emptyList(),
        val links: List<Link> = emptyList()
)