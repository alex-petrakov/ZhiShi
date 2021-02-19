package me.alex.pet.apps.zhishi.presentation.common

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observe(viewLifecycleOwner: LifecycleOwner, onChanged: (T) -> Unit) {
    this.observe(viewLifecycleOwner, Observer { t -> onChanged(t) })
}