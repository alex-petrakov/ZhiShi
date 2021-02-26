package me.alex.pet.apps.zhishi.presentation.common.styledtext.spans

import android.text.style.ClickableSpan
import android.view.View

data class LinkSpan(val ruleId: Long, private val onClickDelegate: (Long) -> Unit) : ClickableSpan() {

    override fun onClick(widget: View) {
        onClickDelegate(ruleId)
    }
}