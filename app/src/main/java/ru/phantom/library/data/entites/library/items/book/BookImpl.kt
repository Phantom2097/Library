package ru.phantom.library.data.entites.library.items.book

import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.domain.library_service.LibraryService

data class BookImpl(
    override val item: LibraryItem,
    override val service: LibraryService,
    override var author: List<String> = emptyList(),
    override var numberOfPages: Int? = null
) : Book(item, service) {

    override fun toString(): String {
        val tempAvailability = service.showAvailability(item.availability)
        val tempNumberOfPages = numberOfPages?.let {
            numberOfPages.toString()
        } ?: "*неизвестно*"

        val tempAuthor = when {
            author.size == 1 -> "автор: ${author[0]}"
            author.size > 1 -> "авторы: ${author.joinToString(separator = ", ", postfix = ".")}"
            else ->"*автор неизвестен*"
        }
        return "Книга: ${item.name} ($tempNumberOfPages стр.) $tempAuthor с id: ${item.id} доступна: $tempAvailability\n"
    }
}