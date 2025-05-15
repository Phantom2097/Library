package ru.phantom.library.presentation.all_items_list.main_recycler.utils

import androidx.recyclerview.widget.DiffUtil
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.AdapterItems
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.AdapterItems.DataItem

class ElementDiffCallback : DiffUtil.ItemCallback<AdapterItems>() {
    override fun areItemsTheSame(
        oldItem: AdapterItems, newItem: AdapterItems
    ): Boolean {
        return when {
            oldItem is DataItem && newItem is DataItem -> {
                oldItem.listElement.item.id == newItem.listElement.item.id
            }

            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItem: AdapterItems, newItem: AdapterItems
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(
        oldItem: AdapterItems, newItem: AdapterItems
    ): Any? {
        return when {
            oldItem is DataItem && newItem is DataItem &&
                    oldItem.listElement.item.availability != newItem.listElement.item.availability -> {
                ItemAvailabilityChange(newItem.listElement.item.availability)
            }
            else -> null
        }
    }

    data class ItemAvailabilityChange(
        val changedAvailability: Boolean
    )
}