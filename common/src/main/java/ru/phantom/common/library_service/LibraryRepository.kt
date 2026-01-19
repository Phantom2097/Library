package ru.phantom.common.library_service

import ru.phantom.common.colors.Colors
import ru.phantom.common.digitize.DigitizationOffice
import ru.phantom.common.entities.library.book.Book
import ru.phantom.common.entities.library.disk.Disk
import ru.phantom.common.entities.library.newspaper.Newspaper

object LibraryRepository {
    private val _booksList by lazy { mutableListOf<Book>() }
    private val _disksList by lazy { mutableListOf<Disk>() }
    private val _newspapersList by lazy { mutableListOf<Newspaper>() }

    // Оцифрованные предметы
    private val _digitizeSet by lazy { LibraryDigitizeMutableSet<DigitizationOffice.DigitalItem>() }

    private var itemsCounter = LibraryRepositoryConsts.START_LIBRARY_ITEM_INDEX

    // Book
    fun addItemBook(book: Book) {
        _booksList.add(book)
    }

    fun getBooksInLibrary(): List<Book> = _booksList.ifEmpty {
        println(Colors.ANSI_RED + "На данный момент в библиотеке нет ни одной книги 🤷‍♂️\n" + Colors.ANSI_RESET)
        emptyList()
    }

    // Newspaper
    fun addItemNewspaper(newspaper: Newspaper) {
        _newspapersList.add(newspaper)
    }

    fun getNewspapersInLibrary(): List<Newspaper> = _newspapersList.ifEmpty {
        println(Colors.ANSI_RED + "На данный момент в библиотеке нет ни одной газеты 🤷‍♂️\n" + Colors.ANSI_RESET)
        emptyList()
    }

    // Disk
    fun addItemDisk(disk: Disk) {
        _disksList.add(disk)
    }

    fun getDisksInLibrary(): List<Disk> = _disksList.ifEmpty {
        println(Colors.ANSI_RED + "На данный момент в библиотеке нет ни одного диска 🤷‍♂️\n" + Colors.ANSI_RESET)
        emptyList()
    }

    fun <LibraryType> addItem(item: LibraryType) {
        when (item) {
            is Book -> _booksList.add(item)
            is Newspaper -> _newspapersList.add(item)
            is Disk -> _disksList.add(item)
            is DigitizationOffice.DigitalItem -> _digitizeSet.add(item)
            else -> {
                println(Colors.ANSI_RED + "Подборки для этого предмета в библиотеке нет!!!" + Colors.ANSI_RESET)
            }
        }
    }

    inline fun <reified T> getItem(items: List<T>, num: Int): T? {
        return items.getOrElse(num) {
            val message = StringBuilder().apply {
                append("${Colors.ANSI_YELLOW}Неверный порядковый номер\n")
                append("\t${Colors.ANSI_CYAN}Попробуйте ещё раз\n${Colors.ANSI_RESET}")
            }.toString()

            println(message)
            null
        }
    }

    // ItemsCounter
    fun getItemsCounter() = itemsCounter++

    fun getDigitizeItems(): List<DigitizationOffice.DigitalItem> = _digitizeSet.toList().ifEmpty {
        println(Colors.ANSI_RED + "На данный момент в библиотеке нет ни одного оцифрованного предмета 🤷‍♂️\n" + Colors.ANSI_RESET)
        emptyList()
    }

    private object LibraryRepositoryConsts {
        const val START_LIBRARY_ITEM_INDEX = 0L
    }
}