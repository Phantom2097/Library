package ru.phantom.library.presentation.selected_item

/**
 * Состояние для DetailFragment
 * @see DetailFragment
 */
sealed class LoadingStateToDetail {

    object Loading : LoadingStateToDetail()
    data class Data(val data: DetailState = DetailState()) : LoadingStateToDetail()
    class Error() : LoadingStateToDetail()
}