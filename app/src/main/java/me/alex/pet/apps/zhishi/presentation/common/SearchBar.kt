package me.alex.pet.apps.zhishi.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import me.alex.pet.apps.zhishi.R

class SearchBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val card: ViewGroup by lazy { findViewById(R.id.card) }

    private val menuBtn: ImageButton by lazy { findViewById(R.id.menu_btn) }

    var onCardClickListener: (() -> Unit)? = null
        set(value) {
            when (value) {
                null -> card.setOnClickListener(null)
                else -> card.setOnClickListener { onCardClickListener?.invoke() }
            }
            field = value
        }

    var onMenuButtonClickListener: (() -> Unit)? = null
        set(value) {
            when (value) {
                null -> menuBtn.setOnClickListener(null)
                else -> menuBtn.setOnClickListener { onMenuButtonClickListener?.invoke() }
            }
            field = value
        }

    init {
        inflate(context, R.layout.view_search_bar, this)
    }
}