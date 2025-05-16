package ru.phantom.common.entities.manager

import ru.phantom.common.entities.shop.Shop

/**
 * Функциональный интерфейс менеджера, который возвращает предмет того же типа, что и магазин.
 */
fun interface Manager<T> {
    fun buy(shop: Shop<T>): T
}