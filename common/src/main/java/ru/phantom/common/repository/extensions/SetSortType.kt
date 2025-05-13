package ru.phantom.common.repository.extensions

import ru.phantom.common.repository.filters.SortType

fun interface SetSortType {
    fun setSortType(sortType: SortType)
}