package ru.phantom.library.domain.main_recycler.adapter

import ru.phantom.library.data.local.models.library.items.BasicLibraryElement

sealed class AdapterItems {
    object LoadItem : AdapterItems()
    data class DataItem(val listElement: BasicLibraryElement) : AdapterItems()
    // Тут можно было бы ещё добавить ошибку
    // Тут можно было бы добавить ещё и текст в конце списка (типа больше элементов нет)
}