package ru.phantom.library.presentation.all_items_list.main_recycler.adapter

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.phantom.library.presentation.main.DisplayStates.GOOGLE_BOOKS
import ru.phantom.library.presentation.main.MainViewModel
import ru.phantom.library.presentation.main.MainViewModel.Companion.LOADING_STATE_NEXT
import ru.phantom.library.presentation.main.MainViewModel.Companion.LOADING_STATE_PREV
import ru.phantom.library.presentation.main.MainViewModel.Companion.LOAD_THRESHOLD
import kotlin.math.abs

class MyScrollListener(
    private val viewModel: MainViewModel,
    private val adapter: LibraryItemsAdapter
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (abs(dy) < SCROLL_NO_REGISTER || viewModel.screenModeState.value == GOOGLE_BOOKS) return

        val isScrollingUp = dy < SCROLL_POSITION_ZERO

        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        val totalItems = adapter.itemCount

        // Подгрузка следующих элементов
        if (!isScrollingUp
            && viewModel.loadingState.value != LOADING_STATE_NEXT
            && lastVisible > totalItems - LOAD_THRESHOLD) {
            Log.d("PAGINATION", "Trigger load next $dy")
            viewModel.loadNext()
        }
        // Подгрузка предыдущих элементов
        if (isScrollingUp
            && viewModel.loadingState.value != LOADING_STATE_PREV
            && firstVisible < LOAD_THRESHOLD) {
            Log.d("PAGINATION", "Trigger load prev $dy")
            viewModel.loadPrev()
        }
        /*
        Было прикольно скрывать floating action button при скроле вниз
         */
    }

    private companion object {
        private const val SCROLL_POSITION_ZERO = 0
        private const val SCROLL_NO_REGISTER = 5
    }
}