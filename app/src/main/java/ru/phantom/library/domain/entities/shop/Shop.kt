package ru.phantom.library.domain.entities.shop

fun interface Shop<out T> {
    fun sell(): T
}