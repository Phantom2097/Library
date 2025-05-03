package ru.phantom.library.domain.main_recycler.adapter

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.phantom.library.domain.main_recycler.adapter.LibraryItemsAdapter.Companion.TYPE_LOAD_BOTTOM
import ru.phantom.library.domain.main_recycler.adapter.LibraryItemsAdapter.Companion.TYPE_LOAD_UP
import ru.phantom.library.presentation.main.MainViewModel
import ru.phantom.library.presentation.main.MainViewModel.Companion.LOAD_THRESHOLD

class MyScrollListener(
    private val viewModel: MainViewModel,
    private val adapter: LibraryItemsAdapter
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy == 0) return

        val isScrollingUp = dy < 0

        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        val totalItems = adapter.itemCount

        // Подгрузка следующих элементов
        if (!isScrollingUp
            && viewModel.loadingState.value != TYPE_LOAD_BOTTOM
            && lastVisible >= totalItems - LOAD_THRESHOLD) {
            Log.d("PAGINATION", "Trigger load next")
            viewModel.loadNext()
        }
        // Подгрузка предыдущих элементов
        else if (isScrollingUp
            && viewModel.loadingState.value != TYPE_LOAD_UP
            && firstVisible <= LOAD_THRESHOLD) {
            Log.d("PAGINATION", "Trigger load prev")
            viewModel.loadPrev()
        }
    }
}