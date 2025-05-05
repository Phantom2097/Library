package ru.phantom.library.domain.entities.library.newspaper.month

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

    companion object {
        fun toMonth(stringType: String?) = stringType?.let {
            entries.find { it.month.equals(stringType, ignoreCase = true)} ?: UNKNOWN
        } ?: UNKNOWN
    }
}