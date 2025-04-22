package ru.phantom.library.presentation.selected_item.states

/**
 * Состояние для DetailFragment
 * @see ru.phantom.library.presentation.selected_item.DetailFragment
 */
sealed class LoadingStateToDetail {

    object Loading : LoadingStateToDetail()
    data class Data(val data: DetailState = DetailState()) : LoadingStateToDetail()
    class Error(val e: String?) : LoadingStateToDetail()
}