package ru.phantom.common.entities.library.disk

enum class Type(private val type: String) : Digitable {
    CD("CD"),
    DVD("DVD"),
    UNKNOWN("*Тип диска неизвестен*");

    override fun getType() = type

    companion object {
        fun getEnumType(stringType: String): Type {
            return entries.find { it.type.equals(stringType, ignoreCase = true)} ?: UNKNOWN
        }
    }
}