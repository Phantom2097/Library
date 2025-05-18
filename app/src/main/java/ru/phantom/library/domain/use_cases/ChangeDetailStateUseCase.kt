package ru.phantom.library.domain.use_cases

import android.accounts.NetworkErrorException
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import ru.phantom.common.entities.library.BasicLibraryElement
import ru.phantom.common.repository.ItemsRepository
import ru.phantom.common.repository.extensions.SimulateRealRepository
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.SHOW_TYPE
import ru.phantom.library.presentation.selected_item.states.DetailState
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail

class ChangeDetailStateUseCase(
    private val repository: ItemsRepository<BasicLibraryElement>
) {
    operator fun invoke(state: DetailState = DetailState()) : Flow<LoadingStateToDetail> {
        return flow {
            if (state.uiType == SHOW_TYPE) {
                emit(LoadingStateToDetail.Loading)
                (repository as? SimulateRealRepository)?.simulateRealRepository()
            }
            emit(LoadingStateToDetail.Data(state))
        }
            .catch { e ->
                emit(handleRepositoryErrors(e))
            }
    }

    private fun handleRepositoryErrors(e: Throwable) : LoadingStateToDetail {
        val errorState = when (e) {
            is NetworkErrorException -> {
                Log.w("DetailState", "Ошибка сети", e)
                LoadingStateToDetail.Error("Ошибка сети, проверьте подключение")
            }

            else -> {
                Log.w("DetailState", "Непредвиденная ошибка", e)
                LoadingStateToDetail.Error(e.message ?: "Unknown error")
            }
        }
        return errorState
    }
}