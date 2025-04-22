package ru.phantom.library.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
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

        binding.recyclerShimmer.isVisible = true
        initList()
    }

    override fun onResume() {
        super.onResume()

        initViewModel()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initViewModel() = viewLifecycleOwner.lifecycleScope.launch {
        launch {
            viewModel.elements.collect { notes ->
                if (notes.isNotEmpty()) binding.recyclerShimmer.isGone = true
                libraryAdapter.submitList(notes)
            }
        }
        launch {
            viewModel.scrollToEnd.collect { scroll ->
                if (scroll) {
                    binding.recyclerMainScreen.post {
                        binding.recyclerMainScreen.smoothScrollToPosition(libraryAdapter.currentList.size)
                        viewModel.scrollToEndReset()
                    }
                }
            }
        }
    }

    companion object {
        // Для списка элементов
        private const val SPAN_COUNT = 2
        private const val SPACES_ITEM_DECORATION_COUNT = 12
    }
}