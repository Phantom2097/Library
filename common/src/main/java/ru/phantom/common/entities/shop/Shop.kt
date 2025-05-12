package ru.phantom.common.entities.shop

fun interface Shop<out T> {
    fun sell(): T
}