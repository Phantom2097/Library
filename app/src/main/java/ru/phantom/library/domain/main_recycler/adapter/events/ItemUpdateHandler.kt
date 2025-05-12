package ru.phantom.library.domain.main_recycler.adapter.events

import ru.phantom.common.entities.library.BasicLibraryElement

/**
 * Осуществляет обновление состояний связанных с элементом, который был изменён
 */
interface ItemUpdateHandler {
    fun updateElement(position: Int, newItem: BasicLibraryElement)
    fun updateDetailStateIfNeeded(newItem: BasicLibraryElement)
}