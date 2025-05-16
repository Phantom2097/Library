package ru.phantom.common.repository.filters

enum class SortType() {
    DEFAULT_SORT,
    SORT_BY_NAME,
    SORT_BY_TIME;

    companion object {
        fun getEnumSortType(name: String) = entries.find { it.name == name } ?: DEFAULT_SORT
    }
}