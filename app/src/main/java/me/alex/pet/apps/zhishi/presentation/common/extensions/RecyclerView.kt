package me.alex.pet.apps.zhishi.presentation.common.extensions

import androidx.recyclerview.widget.RecyclerView

inline fun RecyclerView.ViewHolder.withExistingAdapterPosition(action: (Int) -> Unit) {
    val position = adapterPosition
    if (position != RecyclerView.NO_POSITION) {
        action(position)
    }
}