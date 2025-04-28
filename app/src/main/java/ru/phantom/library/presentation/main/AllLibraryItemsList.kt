package ru.phantom.library.presentation.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.phantom.library.R
import ru.phantom.library.databinding.AllLibraryItemsListBinding
import ru.phantom.library.domain.main_recycler.adapter.LibraryItemsAdapter
import ru.phantom.library.presentation_console.decoration.SpacesItemDecoration

class AllLibraryItemsList() : Fragment(R.layout.all_library_items_list) {

    private var _binding: AllLibraryItemsListBinding? = null
    val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()
    private val libraryAdapter by lazy { LibraryItemsAdapter(viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AllLibraryItemsListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()

        initList()
        initMenuButton()
    }

    private fun initList() {
        val recyclerView = binding.recyclerMainScreen

        with(recyclerView) {
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
            adapter = libraryAdapter
            addItemDecoration(SpacesItemDecoration(SPACES_ITEM_DECORATION_COUNT))
        }

        // Добавляю itemTouchHelper
        val itemTouchHelper = ItemTouchHelper(libraryAdapter.getMySimpleCallback())
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun initMenuButton() {
        binding.sortItemsButton.setOnClickListener {
            val menu = PopupMenu(requireContext(), it)
            menu.menuInflater.inflate(R.menu.recycler_sort_menu, menu.menu)

            menu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.actionSortDefault -> viewModel.setSortType(DEFAULT_SORT)
                    R.id.actionSortByName -> viewModel.setSortType(SORT_BY_NAME)
                    R.id.actionSortByTime -> viewModel.setSortType(SORT_BY_TIME)
                }
                true
            }
            menu.show()
        }
    }

    override fun onDestroy() {
        Log.d("ScrollState", "фрагмент разрушен")
        super.onDestroy()
        _binding = null
    }

    /**
     *  Инициализация слушателей вью модели
     */
    private fun initViewModel() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            launch {
                viewModel.elements.collect { notes ->
                    if (notes.isEmpty()) {
                        binding.apply {
                            sortItemsButton.isGone = true
                            recyclerShimmer.isVisible = true
                            delay(LOAD_DELAY)
                            recyclerShimmer.isGone = true
                            recyclerMainNoElements.isVisible = true
                        }
                    } else {
                        binding.apply {
                            recyclerShimmer.isGone = true
                            sortItemsButton.isVisible = true
                            recyclerMainNoElements.isGone = true
                        }
                        libraryAdapter.submitList(notes)
                    }
                }
            }
            launch {
                viewModel.scrollToEnd.collect { state ->
                    if (state) {
                        val lastPosition = libraryAdapter.itemCount + 1
                        binding.recyclerMainScreen.post {
                            Log.d("ScrollState", "Последняя позиция: $lastPosition")
                            binding.recyclerMainScreen.smoothScrollToPosition(lastPosition)
                        }
                        viewModel.resetScrollToEnd()
                    }
                }
            }
        }
    }

    companion object {
        // Для списка элементов
        private const val LOAD_DELAY = 3000L
        private const val SPAN_COUNT = 2
        private const val SPACES_ITEM_DECORATION_COUNT = 12

        const val SORT_STATE_KEY = "SortStateForRecyclerMain"
        const val DEFAULT_SORT = "DEFAULT_SORT"
        const val SORT_BY_NAME = "SORT_BY_NAME"
        const val SORT_BY_TIME = "SORT_BY_TIME"
    }
}