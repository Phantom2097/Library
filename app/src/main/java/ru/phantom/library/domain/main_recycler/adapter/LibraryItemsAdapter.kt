package ru.phantom.library.domain.main_recycler.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.launch
import ru.phantom.library.data.entites.library.items.BasicLibraryElement
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.DiskImpl
import ru.phantom.library.data.entites.library.items.newspaper.NewspaperImpl
import ru.phantom.library.data.entites.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.library.databinding.LibraryItemRecyclerForMainBinding
import ru.phantom.library.domain.main_recycler.utils.ElementDiffCallback
import ru.phantom.library.domain.main_recycler.view_holder.LibraryViewHolder
import ru.phantom.library.presentation.main.MainViewModel
import ru.phantom.library.presentation.selected_item.LoadingStateToDetail

/**
 * Адаптер для всех элементов библиотеки
 */
class LibraryItemsAdapter(
    private val viewModel: MainViewModel
) : ListAdapter<BasicLibraryElement, LibraryViewHolder>(ElementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val binding = LibraryItemRecyclerForMainBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LibraryViewHolder(binding).apply {
            // Переход на view с конкретным элементом
            binding.root.setOnClickListener {
                val position = adapterPosition
                Log.d("CLICKED", "в адаптере")
                viewModel.onItemClicked(getItem(position))
            }
            // Долго нажатие для изменения видимости элемента
            binding.root.setOnLongClickListener { view ->
                changeAvailabilityClick(view.context, adapterPosition)
                true
            }
        }
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: LibraryViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach { payload ->
                when (payload) {
                    is ElementDiffCallback.ItemAvailabilityChange ->
                        holder.bindAvailability(payload.changedAvailability)
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
        val newLibraryItem = oldItem.item.copy(availability = !oldItem.item.availability)
        val newItem = when (oldItem) {
            is BookImpl -> oldItem.copy(item = newLibraryItem)
            is DiskImpl -> oldItem.copy(item = newLibraryItem)
            is NewspaperImpl -> oldItem.copy(item = newLibraryItem)
            is NewspaperWithMonthImpl -> oldItem.copy(item = newLibraryItem)
            else -> throw IllegalArgumentException("Неверный тип элемента")
        }

        // Обновление списка и состояния
        updateViewModel(position, newItem)

        Log.d(
            "Availability",
            "prev availability = ${oldItem.item.availability}"
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

    /**
     * Осуществляет обновление состояний связанных с элементом, который был изменён
     */
    private fun updateViewModel(
        position: Int,
        newItem: BasicLibraryElement
    ) = viewModel.viewModelScope.launch {

        viewModel.updateElementContent(position, newItem)

        // Если изменилось состояние текущего элемента, то оно меняется и на DetailFragment
        viewModel.detailState.value.takeIf { state ->
            state is LoadingStateToDetail.Data
                    && state.data.id == newItem.item.id
                    && state.data.name == newItem.item.name
        }?.let { state ->
            viewModel.setDetailState(
                (state as LoadingStateToDetail.Data)
                    .data.copy(description = newItem.fullInformation()))
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
                viewModel.selectedRemove(getItem(currPosition))
                viewModel.removeElement(currPosition)
            }
        }
    }

    companion object {
        const val NO_ACTION = 0
    }
}