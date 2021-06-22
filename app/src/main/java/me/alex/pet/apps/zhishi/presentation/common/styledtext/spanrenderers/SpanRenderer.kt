package me.alex.pet.apps.zhishi.presentation.common.styledtext.spanrenderers

import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan

interface SpanRenderer<T> {
    fun convertToAndroidSpans(spans: List<T>): List<PositionAwareSpan>
}