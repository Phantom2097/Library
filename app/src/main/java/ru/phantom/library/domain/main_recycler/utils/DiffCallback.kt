package ru.phantom.library.domain.main_recycler.utils

import androidx.recyclerview.widget.DiffUtil
import ru.phantom.library.domain.main_recycler.ViewTypedLibraryWrapper

class DiffCallback(
    private val oldList: List<ViewTypedLibraryWrapper>,
    private val newList: List<ViewTypedLibraryWrapper>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition].getItemable()
        val newItem = newList[newItemPosition].getItemable()
        return oldItem.getId() == newItem.getId()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition].getItemable()
        val newItem = newList[newItemPosition].getItemable()

        return oldItem.getAvailability() == newItem.getAvailability()
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldList[oldItemPosition].getItemable()
        val newItem = newList[newItemPosition].getItemable()

        return when {
            oldItem.getAvailability() != newItem.getAvailability() -> ItemAvailabilityChange(newItem.getAvailability())
            else -> null
        }
    }
    data class ItemAvailabilityChange(
        val changedAvailability: Boolean
    )
}



