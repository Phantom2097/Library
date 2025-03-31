package ru.phantom.library.data.entites.library.items.newspaper_with_month

import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
import ru.phantom.library.domain.library_service.LibraryService

abstract class NewspaperWithMonth(
    item: LibraryItem,
    libraryService: LibraryService
) :
    Newspaper(item, libraryService)
{
    abstract var issueMonth: Month
}

enum class Month(private val month: String) {
    JANUARY("Январь"),
    FEBRUARY("Февраль"),
    MARCH("Март"),
    APRIL("Апрель"),
    MAY("Май"),
    JUNE("Июнь"),
    JULY("Июль"),
    AUGUST("Август"),
    SEPTEMBER("Сентябрь"),
    OCTOBER("Октябрь"),
    NOVEMBER("Ноябрь"),
    DECEMBER("Декабрь"),
    UNKNOWN("*Неизвестно*");

    fun getMonth(): String = month

    //fun getMonthFromNumber(num: Int): String = (entries.getOrNull(num - 1)?: UNKNOWN).month
}