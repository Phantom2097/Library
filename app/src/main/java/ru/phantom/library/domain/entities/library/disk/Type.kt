package ru.phantom.library.domain.entities.library.disk


enum class Type(private val type: String): Digitable {
    CD("CD"),
    DVD("DVD"),
    UNKNOWN("*Тип диска неизвестен*");

    override fun getType() = type
}