package ru.phantom.library.domain.main_recycler.adapter

import ru.phantom.library.data.local.models.library.items.BasicLibraryElement

/**
 * Класс обёртка для элементов списка главного экрана
 */
sealed class AdapterItems {
    /**
     * Элемент списка, представляющий собой шиммер
     */
    object LoadItem : AdapterItems()

    /**
     * Обычный элемент списка, с которым осуществляется взаимодействие
     */
    data class DataItem(val listElement: BasicLibraryElement) : AdapterItems()

    // Если вы видете эту надпись, оно будет нужно
    /**
     * Элементы списка представляющие собой книги полученные из гугл букс
     */
//    data class BooksGoogleApi(val book: Book) : AdapterItems()
    // Тут можно было бы ещё добавить ошибку
    // Тут можно было бы добавить ещё и текст в конце списка (типа больше элементов нет)
}