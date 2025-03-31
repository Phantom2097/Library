package ru.phantom.library.domain.item_mappers.digitize

import presentation.colors.Colors.ANSI_PURPLE
import presentation.colors.Colors.ANSI_RESET
import ru.phantom.library.data.entites.library.Showable
import ru.phantom.library.data.entites.library.items.Digitable
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.Disk
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
import ru.phantom.library.domain.item_mappers.digitize.DigitizationOffice.DigitalType.CD

sealed interface DigitizationOffice {
    data class DigitalItem(val item: Showable, private val type: Digitable = CD): Showable {
        override fun briefInformation(): String {
            return buildString {
                append(item.briefInformation())
                append(ANSI_PURPLE)
                append("\n\t\t**Оцифрована на ${type.getType()}**")
                append(ANSI_RESET)
            }
        }

        override fun fullInformation(): String {
            return this.toString()
        }

        override fun toString(): String {
            return buildString {
                append(ANSI_PURPLE)
                append("Оцифрованный предмет библиотеки, тип диска $type:\n")
                append(ANSI_RESET)
                append(item.fullInformation())
            }
        }
    }
    class DigitizeBook(
        private val book: BookImpl,
        override var type: Digitable = CD
    ) : DigitizationOffice, Disk {
        override fun toDigitize(): DigitalItem {
            return DigitalItem(book, type)
        }
    }

    class DigitizeNewspaper(
        private val newspaper: Newspaper,
        override var type: Digitable = CD
    ) : DigitizationOffice, Disk {
        override fun toDigitize(): DigitalItem {
            return DigitalItem(newspaper, type)
        }
    }

    enum class DigitalType(private val type: String): Digitable {
        CD("CD");

        override fun getType() = type
    }

    fun toDigitize(): DigitalItem = when (this) {
        is DigitizeBook -> this.toDigitize()
        is DigitizeNewspaper -> this.toDigitize()
    }
}