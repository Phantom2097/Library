package ru.phantom.library.di

import dagger.Component
import ru.phantom.data.di.DataComponent
import ru.phantom.library.presentation.all_items_list.AllLibraryItemsList
import ru.phantom.library.presentation.google_books.SettingQueryFilters
import ru.phantom.library.presentation.main.MainActivity
import ru.phantom.library.presentation.selected_item.DetailFragment

@ViewModelScope
@Component(
    modules = [AppModule::class, UseCasesModule::class, AdapterModule:: class],
    dependencies = [DataComponent::class]
)
interface AppComponent {

    fun inject(activity: MainActivity)
    fun inject(fragment: AllLibraryItemsList)
    fun inject(fragment: DetailFragment)
    fun inject(fragment: SettingQueryFilters)
}