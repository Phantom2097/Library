package ru.phantom.library.di

import dagger.Module
import dagger.Provides
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.LibraryItemsAdapter
import ru.phantom.library.presentation.all_items_list.main_recycler.adapter.factory.MyEdgeFactory
import ru.phantom.library.presentation.main.MainViewModel

@Module
class AdapterModule {

    @Provides
    fun provideLibraryItemsAdapter(
        viewModel: MainViewModel
    ) : LibraryItemsAdapter = LibraryItemsAdapter(viewModel)

    @Provides
    fun provideMyEdgeFactory(
        viewModel: MainViewModel
    ) : MyEdgeFactory = MyEdgeFactory(viewModel)
}