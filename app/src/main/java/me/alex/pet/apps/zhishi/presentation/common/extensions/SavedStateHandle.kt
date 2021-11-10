package me.alex.pet.apps.zhishi.presentation.common.extensions

import androidx.lifecycle.SavedStateHandle

fun <T> SavedStateHandle.getOrThrow(key: String): T {
    return get<T>(key) ?: throw IllegalStateException("No value for the given key: $key")
}