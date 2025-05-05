package ru.phantom.library.domain.main_recycler.adapter

import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
            && lastVisible >= totalItems - LOAD_THRESHOLD) {
            Log.d("PAGINATION", "Trigger load next")
            viewModel.loadNext()
        }
        // Подгрузка предыдущих элементов
        else if (isScrollingUp
            && firstVisible <= LOAD_THRESHOLD) {
            Log.d("PAGINATION", "Trigger load prev")
            viewModel.loadPrev()
        }
        /*
        Было прикольно скрывать floating action button при скроле вниз
         */
    }
}