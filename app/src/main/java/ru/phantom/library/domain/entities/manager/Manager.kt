package ru.phantom.library.domain.entities.manager

import ru.phantom.library.domain.entities.shop.Shop

/**
 * Функциональный интерфейс менеджера, который возвращает предмет того же типа, что и магазин.
 */
fun interface Manager<T> {
    fun buy(shop: Shop<T>): T
}