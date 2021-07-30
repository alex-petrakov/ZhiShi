package me.alex.pet.apps.zhishi.presentation.rules

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RulesToDisplay(val ids: List<Long>, val selectionIndex: Int) : Parcelable {

    init {
        require(ids.isNotEmpty())
        require(selectionIndex in ids.indices)
    }
}