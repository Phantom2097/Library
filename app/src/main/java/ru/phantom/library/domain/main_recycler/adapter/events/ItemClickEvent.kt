package ru.phantom.library.domain.main_recycler.adapter.events

import android.content.Context
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement

interface ItemClickEvent {
    fun onItemClick(element: BasicLibraryElement)
    /**
     * Функция выполняет обновление поля availability выбранного элемента,
     * а также вызывает обновление состояний списка и детального фрагмента в случае
     * совпадения id и name
     */
    fun onItemLongClick(context: Context?, element: BasicLibraryElement) : BasicLibraryElement
    fun onItemSwiped(elementId: Long)
}