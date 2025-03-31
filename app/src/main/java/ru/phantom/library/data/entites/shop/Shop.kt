package ru.phantom.library.data.entites.shop

fun interface Shop<out T> {
    fun sell(): T
}