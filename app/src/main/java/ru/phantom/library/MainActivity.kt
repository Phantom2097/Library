package ru.phantom.library

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import ru.phantom.library.data.repository.LibraryRepository
import ru.phantom.library.data.entites.library.items.Itemable
import ru.phantom.library.databinding.ActivityMainBinding
import ru.phantom.library.domain.library_service.LibraryService
import ru.phantom.library.domain.main_recycler.adapter.LibraryItemsAdapter
import ru.phantom.library.presentation.decoration.SpacesItemDecoration
import ru.phantom.library.presentation.main.createBooks
import ru.phantom.library.presentation.main.createDisks
import ru.phantom.library.presentation.main.createNewspapers

class MainActivity : AppCompatActivity() {

    private val libraryAdapter = LibraryItemsAdapter()
    private var items = mutableListOf<Itemable>()

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = binding.recyclerMainScreen

        with(recyclerView) {
            layoutManager = GridLayoutManager(context, 2)
            adapter = libraryAdapter
            addItemDecoration(SpacesItemDecoration(8))
        }

        // Добавляю itemTouchHelper
        val itemTouchHelper = ItemTouchHelper( libraryAdapter.getMySimpleCallback() )
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Создаю элементы для отображения
        val libraryService = LibraryService()
        createBooks(libraryService)
        createNewspapers(libraryService)
        createDisks(libraryService)

        items.addAll(LibraryRepository.getBooksInLibrary())
        items.addAll(LibraryRepository.getNewspapersInLibrary())
        items.addAll(LibraryRepository.getDisksInLibrary())

        libraryAdapter.addItems(items)
    }
}