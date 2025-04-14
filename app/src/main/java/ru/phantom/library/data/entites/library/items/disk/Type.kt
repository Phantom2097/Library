package ru.phantom.library.data.entites.library.items.disk


enum class Type(private val type: String): Digitable {
    CD("CD"),
    DVD("DVD"),
    UNKNOWN("*Тип диска неизвестен*");

    override fun getType() = type
}