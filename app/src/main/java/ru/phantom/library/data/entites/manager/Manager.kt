package ru.phantom.library.data.entites.manager

import data.entites.shop.Shop

fun interface Manager<T> {
    fun buy(shop: Shop<T>): T
}