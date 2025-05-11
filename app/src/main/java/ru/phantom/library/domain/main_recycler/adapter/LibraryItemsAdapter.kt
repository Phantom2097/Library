package ru.phantom.library.domain.main_recycler.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.phantom.library.R
import ru.phantom.library.databinding.LibraryItemRecyclerForMainBinding
import ru.phantom.library.domain.main_recycler.adapter.AdapterItems.DataItem
import ru.phantom.library.domain.main_recycler.adapter.events.ItemClickEvent
import ru.phantom.library.domain.main_recycler.adapter.events.ItemUpdateHandler
import ru.phantom.library.domain.main_recycler.utils.ElementDiffCallback
import ru.phantom.library.domain.main_recycler.view_holder.LibraryViewHolder
import ru.phantom.library.domain.main_recycler.view_holder.LoadingViewHolder

/**
 * Адаптер для всех элементов библиотеки
 */
class LibraryItemsAdapter(
    private val itemClickListener: ItemClickEvent,
    private val itemUpdateHandler: ItemUpdateHandler,
) : ListAdapter<AdapterItems, ViewHolder>(ElementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_LOAD -> LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.shimmer_for_paggination, parent, false)
            )

            else -> {
                val binding = LibraryItemRecyclerForMainBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                LibraryViewHolder(binding).apply {
                    binding.root.setOnClickListener {
                        val position = adapterPosition
                        Log.d("CLICKED", "в адаптере")
                        (getItem(position) as? DataItem)?.let { dataItem ->
                            itemClickListener.onItemClick(dataItem.listElement)
                        }
                    }
                    binding.root.setOnLongClickListener { view ->
                        val position = adapterPosition
                        (getItem(position) as? DataItem)?.let { dataItem ->
                            val newItem = itemClickListener.onItemLongClick(view.context, dataItem.listElement)
                            itemUpdateHandler.updateElement(position, newItem)
                        }
                        true
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is DataItem -> TYPE_ITEM
            else -> TYPE_LOAD
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
     * Callback для ItemTouchCallback, чтобы свайпать элементы
     */
    fun getMySimpleCallback() = MySimpleCallback()

    inner class MySimpleCallback : SimpleCallback(
        ACTION_STATE_IDLE,
        LEFT or RIGHT
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
                itemClickListener.onItemSwiped(removedItem.listElement.item.id)
            }
        }

        // Запрет удаления Шиммера
        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: ViewHolder
        ): Int = if (viewHolder.itemViewType == TYPE_ITEM) {
            super.getSwipeDirs(recyclerView, viewHolder)
        } else {
            ACTION_STATE_IDLE
        }
    }

    companion object {
        // Перенести в Enum будет лучше
        const val TYPE_ITEM = 0
        const val TYPE_LOAD = 1
    }
}