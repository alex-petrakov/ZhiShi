package me.alex.pet.apps.zhishi.presentation.common

import android.text.SpannedString
import me.alex.pet.apps.zhishi.domain.StyledText

fun StyledText.toSpannedString(): SpannedString {
    return SpannedString(this.string) // TODO: take styles into account
}