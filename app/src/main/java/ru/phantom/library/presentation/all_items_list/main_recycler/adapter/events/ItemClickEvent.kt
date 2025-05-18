package ru.phantom.library.presentation.all_items_list.main_recycler.adapter.events

import android.content.Context
import ru.phantom.common.entities.library.BasicLibraryElement

interface ItemClickEvent {
    fun onItemClick(element: BasicLibraryElement)
    /**
     * Функция выполняет обновление поля availability выбранного элемента,
     * а также вызывает обновление состояний списка и детального фрагмента в случае
     * совпадения id и name
     */
    fun onItemLongClick(context: Context?, position: Int, element: BasicLibraryElement)
    fun onItemSwiped(elementId: Long)
}