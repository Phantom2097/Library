package ru.phantom.library.domain.main_recycler.adapter.events

import ru.phantom.library.data.local.models.library.items.BasicLibraryElement

/**
 * Осуществляет обновление состояний связанных с элементом, который был изменён
 */
interface ItemUpdateHandler {
    fun updateElement(position: Int, newItem: BasicLibraryElement)
    fun updateDetailStateIfNeeded(newItem: BasicLibraryElement)
}