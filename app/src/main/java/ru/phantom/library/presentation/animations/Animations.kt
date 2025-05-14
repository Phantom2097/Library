package ru.phantom.library.presentation.animations

import android.widget.Button
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton


internal object Animations {

    private const val ANIMATION_DURATION = 500L
    private const val DISABLE_ALPHA = 0.3f
    private const val ENABLE_ALPHA = 1.0f

    private const val SIZE_GONE = 0.0f
    private const val SIZE_FULL_VISIBLE = 1.0f

    fun FloatingActionButton.animationForAddButton(
        state: Boolean
    ) {
        animate()
            .scaleX(if (!state) SIZE_FULL_VISIBLE else SIZE_GONE)
            .scaleY(if (!state) SIZE_FULL_VISIBLE else SIZE_GONE)
            .setDuration(ANIMATION_DURATION)
            .withStartAction {
                if (!state) isVisible = true
            }
            .withEndAction {
                isGone = state
            }
            .start()
    }

    fun Button.animationEnableButton() {
        animate()
            .alpha(ENABLE_ALPHA)
            .setDuration(ANIMATION_DURATION)
            .withEndAction {
                isClickable = true
            }
            .start()
    }

    fun Button.animationDisableButton() {
        animate()
            .alpha(DISABLE_ALPHA)
            .setDuration(ANIMATION_DURATION)
            .withStartAction {
                isClickable = false
            }
    }

    fun TextView.textChange(mode: String) {
        animate()
            .alpha(DISABLE_ALPHA)
            .setDuration(ANIMATION_DURATION / 2)
            .withEndAction {
                text = mode
                animate()
                    .alpha(ENABLE_ALPHA)
                    .setDuration(ANIMATION_DURATION / 2)
                    .start()
            }
            .start()
    }
}