package ru.phantom.common.digitize

import ru.phantom.common.colors.Colors.ANSI_PURPLE
import ru.phantom.common.colors.Colors.ANSI_RESET
import ru.phantom.common.entities.library.Showable
import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.entities.library.disk.Digitable
import ru.phantom.common.entities.library.disk.Disk
import ru.phantom.common.entities.library.newspaper.Newspaper
import ru.phantom.common.digitize.DigitizationOffice.DigitalType.CD

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
        private val book: Book,
        override var type: Digitable = CD
    ) : DigitizationOffice, Disk(book.item, book.service) {
        override fun toDigitize(): DigitalItem {
            return DigitalItem(book, type)
        }
    }

    class DigitizeNewspaper(
        private val newspaper: Newspaper,
        override var type: Digitable = CD
    ) : DigitizationOffice, Disk(newspaper.item, newspaper.service) {
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