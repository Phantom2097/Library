package ru.phantom.library.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.phantom.library.presentation.main.MainViewModel
import javax.inject.Provider

@Module
class AppModule {

    @ViewModelScope
    @Provides
    fun provideMainViewModel(
        impl: MainViewModel
    ): ViewModel = impl

    @ViewModelScope
    @Provides
    fun provideViewModelFactory(
        viewModelProvider: Provider<ViewModel>
    ) : ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModelProvider.get() as T
            }
        }
    }

}