package ru.phantom.library.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.phantom.library.data.entites.library.items.BasicLibraryElement

class MainViewModel : ViewModel() {
    private val _elements = MutableLiveData<List<BasicLibraryElement>>(emptyList())
    val elements: LiveData<List<BasicLibraryElement>> = _elements

    fun updateElements(list: List<BasicLibraryElement>) {
        val oldList = _elements.value
        _elements.postValue(oldList?.plus(list) ?: list)
    }

    fun updateElementContent(position: Int, newItem: BasicLibraryElement) {
        val oldList = _elements.value?.toMutableList()

        oldList?.set(position, newItem)

        oldList?.let {
            _elements.value = it
        }
    }

    fun removeElement(position: Int) {
        val newList = _elements.value?.toMutableList() ?: mutableListOf()

        if (position in newList.indices) {
            newList.removeAt(position)
        }

        Log.d("Size", "prev size = ${_elements.value?.size}")
        Log.d("Size", "new size = ${newList.size}")

        _elements.value = newList
    }
}