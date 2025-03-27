package ru.phantom.library.data.entites.library.items.newspaper_with_month

interface NewspaperWithMonth {
    var issueMonth: Month
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