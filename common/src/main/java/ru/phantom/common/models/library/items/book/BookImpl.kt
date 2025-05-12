package ru.phantom.common.models.library.items.book

import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.library_service.LibraryService

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
            author.size == 1 -> {
                val tempAuthor = author[0].ifBlank { "Неизвестен" }
                "Автор: $tempAuthor"
            }

            author.size > 1 -> "Авторы: ${author.joinToString(separator = ", ", postfix = ".")}"
            else -> "*автор неизвестен*"
        }

        return buildString {
            append("Книга: ${item.name}\n")
            append("($tempNumberOfPages стр.)\n")
            append("$tempAuthor\n")
            append("Доступна: $tempAvailability")
        }
    }
}