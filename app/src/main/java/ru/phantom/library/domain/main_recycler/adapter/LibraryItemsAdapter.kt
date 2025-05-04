package ru.phantom.library.domain.main_recycler.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.phantom.library.R
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.data.local.models.library.items.book.BookImpl
import ru.phantom.library.data.local.models.library.items.disk.DiskImpl
import ru.phantom.library.data.local.models.library.items.newspaper.NewspaperImpl
import ru.phantom.library.data.local.models.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.library.databinding.LibraryItemRecyclerForMainBinding
import ru.phantom.library.domain.main_recycler.adapter.AdapterItems.DataItem
import ru.phantom.library.domain.main_recycler.adapter.AdapterItems.LoadItem
import ru.phantom.library.domain.main_recycler.utils.ElementDiffCallback
import ru.phantom.library.domain.main_recycler.view_holder.ErrorViewHolder
import ru.phantom.library.domain.main_recycler.view_holder.LibraryViewHolder
import ru.phantom.library.domain.main_recycler.view_holder.LoadingViewHolder
import ru.phantom.library.presentation.main.MainViewModel
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail

/**
 * Адаптер для всех элементов библиотеки
 */
class LibraryItemsAdapter(
    private val viewModel: MainViewModel
) : ListAdapter<AdapterItems, ViewHolder>(ElementDiffCallback()) {

    private var isLoading = TYPE_ITEM

    fun setLoading(state: Int) {
        when (state) {
            TYPE_LOAD_BOTTOM -> submitList(currentList + LoadItem)
            TYPE_LOAD_UP -> submitList(listOf(LoadItem) + currentList)
        }
        isLoading = state
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_LOAD_UP, TYPE_LOAD_BOTTOM -> LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.shimmer_for_paggination, parent, false)
            )

            TYPE_ITEM -> {
                val binding = LibraryItemRecyclerForMainBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LibraryViewHolder(binding).apply {
                    // Переход на view с конкретным элементом
                    binding.root.setOnClickListener {
                        val position = adapterPosition
                        Log.d("CLICKED", "в адаптере")
                        viewModel.onItemClicked((getItem(position) as DataItem).listElement)
                    }
                    // Долго нажатие для изменения видимости элемента
                    binding.root.setOnLongClickListener { view ->
                        changeAvailabilityClick(view.context, adapterPosition)
                        true
                    }
                }
            }

            else -> {
                ErrorViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shimmer_for_paggination, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading == TYPE_LOAD_BOTTOM && position == currentList.lastIndex) {
            TYPE_LOAD_BOTTOM
        } else if (isLoading == TYPE_LOAD_UP && position == ZERO_POSITION) {
            TYPE_LOAD_UP
        } else {
            TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is LibraryViewHolder -> (getItem(position) as? DataItem)?.let {
                holder.bind(it.listElement)
            }
            else -> {
                Log.d("PAGINATION", "Shimmer add")
            }
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            when (holder) {
                is LibraryViewHolder -> {
                    payloads.forEach { payload ->
                        when (payload) {
                            is ElementDiffCallback.ItemAvailabilityChange ->
                                holder.bindAvailability(payload.changedAvailability)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

    /**
     * Функция выполняет обновление поля availability выбранного элемента,
     * а также вызывает обновление состояний списка и детального фрагмента в случае
     * совпадения id и name
     */
    private fun changeAvailabilityClick(context: Context, position: Int) {
        if (position !in currentList.indices) return

        val oldItem = getItem(position)
        if (oldItem is DataItem) {
            val element = oldItem.listElement

            val newLibraryItem = element.item.copy(availability = !element.item.availability)
            val newItem = when (oldItem.listElement) {
                is BookImpl -> element.copy(item = newLibraryItem)
                is DiskImpl -> element.copy(item = newLibraryItem)
                is NewspaperImpl -> element.copy(item = newLibraryItem)
                is NewspaperWithMonthImpl -> element.copy(item = newLibraryItem)
                else -> throw IllegalArgumentException("Неверный тип элемента")
            }

            // Обновление списка и состояния
            updateViewModel(position, newItem)

            Log.d(
                "Availability",
                "prev availability = ${element.item.availability}"
            )
            Log.d(
                "Availability",
                "new availability = ${newItem.item.availability}"
            )

            makeText(
                context,
                newItem.briefInformation(),
                LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Осуществляет обновление состояний связанных с элементом, который был изменён
     */
    private fun updateViewModel(
        position: Int,
        newItem: BasicLibraryElement
    ) {

        viewModel.updateElementContent(position, newItem)

        // Если изменилось состояние текущего элемента, то оно меняется и на DetailFragment
        viewModel.detailState.value.takeIf { state ->
            state is LoadingStateToDetail.Data
                    && state.data.id == newItem.item.id
                    && state.data.name == newItem.item.name
        }?.let { state ->
            viewModel.setDetailState(
                (state as LoadingStateToDetail.Data)
                    .data.copy(description = newItem.fullInformation())
            )
        }
    }

    /**
     * Callback для ItemTouchCallback, чтобы свайпать элементы
     */
    fun getMySimpleCallback() = MySimpleCallback()

    inner class MySimpleCallback : SimpleCallback(
        NO_ACTION,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: ViewHolder,
            target: ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
            val currPosition = viewHolder.adapterPosition

            if (currPosition != RecyclerView.NO_POSITION) {
                val removedItem = getItem(currPosition) as DataItem
                viewModel.removeElementById(removedItem.listElement.item.id)
            }
        }

        // Запрет удаления Шиммера
        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: ViewHolder
        ): Int = if (viewHolder.itemViewType == TYPE_ITEM) {
            super.getSwipeDirs(recyclerView, viewHolder)
        } else {
            NO_ACTION
        }
    }

    companion object {
        const val NO_ACTION = 0

        const val ZERO_POSITION = 0

        const val TYPE_ITEM = 0
        const val TYPE_LOAD_UP = 1
        const val TYPE_LOAD_BOTTOM = 2
    }
}