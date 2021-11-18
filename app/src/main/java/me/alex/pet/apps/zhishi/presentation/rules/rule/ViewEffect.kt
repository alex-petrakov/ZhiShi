package me.alex.pet.apps.zhishi.presentation.rules.rule

import android.net.Uri

sealed class ViewEffect {
    data class ShareRuleText(val text: String) : ViewEffect()
    data class ShareRuleSnapshot(val snapshotUri: Uri) : ViewEffect()
    object RequestViewSnapshot : ViewEffect()
}