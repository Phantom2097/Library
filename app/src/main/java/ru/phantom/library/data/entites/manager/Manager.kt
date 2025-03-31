package ru.phantom.library.data.entites.manager

import ru.phantom.library.data.entites.shop.Shop

fun interface Manager<T> {
    fun buy(shop: Shop<T>): T
}