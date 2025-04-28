package ru.phantom.library.data.repository.extensions

import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.data.repository.DBRepository
import ru.phantom.library.data.repository.ItemsRepository

fun ItemsRepository<BasicLibraryElement>.setSortType(sortType: String) {
    if (this is DBRepository) {
        setSortType(sortType)
    }
}