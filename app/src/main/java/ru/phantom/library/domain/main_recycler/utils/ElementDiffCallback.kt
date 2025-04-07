package ru.phantom.library.domain.main_recycler.utils

import androidx.recyclerview.widget.DiffUtil
import ru.phantom.library.data.entites.library.items.BasicLibraryElement

class ElementDiffCallback : DiffUtil.ItemCallback<BasicLibraryElement>() {
    override fun areItemsTheSame(
        oldItem: BasicLibraryElement,
        newItem: BasicLibraryElement
    ): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(
        oldItem: BasicLibraryElement,
        newItem: BasicLibraryElement
    ): Boolean {
        return oldItem.item == newItem.item
    }

    override fun getChangePayload(
        oldItem: BasicLibraryElement,
        newItem: BasicLibraryElement
    ): Any? {
        return when {
            oldItem.item.availability != newItem.item.availability -> ItemAvailabilityChange(
                newItem.item.availability
            )
            else -> null
        }
    }

    data class ItemAvailabilityChange(
        val changedAvailability: Boolean
    )
}