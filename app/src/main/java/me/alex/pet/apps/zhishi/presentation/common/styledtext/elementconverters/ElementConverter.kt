package me.alex.pet.apps.zhishi.presentation.common.styledtext.elementconverters

import me.alex.pet.apps.zhishi.presentation.common.styledtext.PositionAwareSpan

interface ElementConverter<T> {
    fun convertToSpan(element: T): PositionAwareSpan?
}