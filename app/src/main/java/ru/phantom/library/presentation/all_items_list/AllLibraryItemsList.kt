package ru.phantom.library.presentation.all_items_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.edit
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import jakarta.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.phantom.common.repository.filters.SortType
import ru.phantom.library.R
import ru.phantom.library.databinding.AllLibraryItemsListBinding
import ru.phantom.library.di.AppComponentProvider
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.LibraryItemsAdapter
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.MyScrollListener
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.decoration.SpacesItemDecoration
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.factory.MyEdgeFactory
import ru.phantom.library.presentation.main.DisplayStates
import ru.phantom.library.presentation.main.MainViewModel

class AllLibraryItemsList : Fragment(R.layout.all_library_items_list) {

    private var _binding: AllLibraryItemsListBinding? = null
    val binding get() = _binding!!

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by activityViewModels<MainViewModel> { viewModelFactory }

    private val libraryAdapter by lazy { LibraryItemsAdapter(viewModel) }

    private val sharedPref by lazy {
        requireContext().getSharedPreferences(
            SORT_STATE_KEY,
            Context.MODE_PRIVATE
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val component = (requireContext().applicationContext as AppComponentProvider).getAppComponent()
        component.inject(this)
    }

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
        val recyclerView = binding.recyclerMainScreen.apply {
            edgeEffectFactory = MyEdgeFactory(viewModel)
        }

        val sortType = sharedPref.getString(SORT_STATE_KEY, SortType.DEFAULT_SORT.name)
            ?: SortType.DEFAULT_SORT.name
        viewModel.setSortType(sortType)

        with(recyclerView) {
            layoutManager = GridLayoutManager(context, SPAN_COUNT).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (libraryAdapter.getItemViewType(position)) {
                            LibraryItemsAdapter.Companion.TYPE_LOAD -> SPAN_COUNT
                            else -> SPAN_COUNT_FOR_ITEM
                        }
                    }
                }
            }
            adapter = libraryAdapter
            addItemDecoration(SpacesItemDecoration(SPACES_ITEM_DECORATION_COUNT))
            addOnScrollListener(MyScrollListener(viewModel, libraryAdapter))

            // Добавляю itemTouchHelper
            val itemTouchHelper = ItemTouchHelper(libraryAdapter.getMySimpleCallback())
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun initMenuButton() {
        binding.sortItemsButton.setOnClickListener { view ->
            PopupMenu(requireContext(), view).apply {
                menuInflater.inflate(R.menu.recycler_sort_menu, menu)

                setOnMenuItemClickListener { item ->
                    val sortType = when (item.itemId) {
                        R.id.actionSortByName -> SortType.SORT_BY_NAME
                        R.id.actionSortByTime -> SortType.SORT_BY_TIME
                        else -> SortType.DEFAULT_SORT
                    }.name
                    viewModel.setSortType(sortType)
                    sharedPref.edit { putString(SORT_STATE_KEY, sortType) }
                    Log.d("SORT_TYPE", sortType)
                    true
                }
                show()
            }
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
                viewModel.elements.collectLatest { notes ->
                    libraryAdapter.submitList(notes)
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
                            if (viewModel.screenModeState.value == DisplayStates.MY_LIBRARY) sortItemsButton.isVisible =
                                true
                            recyclerMainNoElements.isGone = true
                        }
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
            launch {
                viewModel.errorRequest.collect { message ->
                    message?.let {
                        binding.recyclerMainNoElements.text = message
                    }
                }
            }
        }
    }

    companion object {
        // Для списка элементов
        private const val LOAD_DELAY = 3000L
        const val SPAN_COUNT = 2
        private const val SPAN_COUNT_FOR_ITEM = 1
        private const val SPACES_ITEM_DECORATION_COUNT = 12

        const val SORT_STATE_KEY = "SortStateForRecyclerMain"
    }
}