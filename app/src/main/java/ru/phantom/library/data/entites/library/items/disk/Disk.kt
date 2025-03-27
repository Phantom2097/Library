package ru.phantom.library.data.entites.library.items.disk

import ru.phantom.library.data.entites.library.items.Digitable

interface Disk {
    var type: Digitable
}

enum class Type(private val type: String): Digitable {
    CD("CD"),
    DVD("DVD"),
    UNKNOWN("*Тип диска неизвестен*");

    override fun getType() = type
}