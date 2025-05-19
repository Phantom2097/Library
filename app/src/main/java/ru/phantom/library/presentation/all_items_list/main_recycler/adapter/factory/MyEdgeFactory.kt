package ru.phantom.library.presentation.all_items_list.main_recycler.adapter.factory

import android.widget.EdgeEffect
import androidx.recyclerview.widget.RecyclerView
import ru.phantom.library.presentation.main.DisplayStates
import ru.phantom.library.presentation.main.MainViewModel

class MyEdgeFactory(
    private val viewModel: MainViewModel
): RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(view.context) {
            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                if (viewModel.screenModeState.value != DisplayStates.MY_LIBRARY) return
                if (direction == DIRECTION_TOP) {
                    viewModel.loadPrev()
                } else if (direction == DIRECTION_BOTTOM) {
                    viewModel.loadNext()
                }
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                if (viewModel.screenModeState.value != DisplayStates.MY_LIBRARY) return
                if (direction == DIRECTION_TOP) {
                    viewModel.loadPrev()
                } else if (direction == DIRECTION_BOTTOM) {
                    viewModel.loadNext()
                }
            }
        }
    }
}