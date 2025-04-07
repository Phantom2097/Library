package ru.phantom.library.presentation.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.BasicLibraryElement
import ru.phantom.library.data.repository.LibraryRepository
import ru.phantom.library.databinding.ActivityMainBinding
import ru.phantom.library.databinding.BottomSheetForAddLibraryItemBinding
import ru.phantom.library.domain.library_service.LibraryElementFactory
import ru.phantom.library.domain.library_service.LibraryService
import ru.phantom.library.domain.main_recycler.adapter.LibraryItemsAdapter
import ru.phantom.library.presentation.selected_item.SelectedItemActivity
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.CREATE_TYPE
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.DEFAULT_ID
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.DEFAULT_IMAGE
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.SELECTED_ID
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.SELECTED_NAME
import ru.phantom.library.presentation.selected_item.SelectedItemActivity.Companion.TYPE_KEY
import ru.phantom.library.presentation_console.decoration.SpacesItemDecoration
import ru.phantom.library.presentation_console.main.createBooks
import ru.phantom.library.presentation_console.main.createDisks
import ru.phantom.library.presentation_console.main.createNewspapers

class MainActivity : AppCompatActivity() {

    private val libraryAdapter by lazy { LibraryItemsAdapter(viewModel) }
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var viewModel: MainViewModel

    private val startForResult = resultHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Инициализирую view модель
        initViewModel()

        // Делаю то, что связано с UI
        initUi()

        // Создаю элементы для списка
        createItems()
    }

    private fun initUi() {
        val recyclerView = binding.recyclerMainScreen

        with(recyclerView) {
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
            adapter = libraryAdapter
            addItemDecoration(SpacesItemDecoration(SPACES_ITEM_DECORATION_COUNT))
        }

        // Добавляю itemTouchHelper
        val itemTouchHelper = ItemTouchHelper(libraryAdapter.getMySimpleCallback())
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val addButton = binding.mainAddButton

        addButton.setOnClickListener {
            showAddItemBottomSheet()
        }
    }

    private fun createItems() {
        // Создаю элементы для отображения
        val libraryService = LibraryService
        createBooks(libraryService)
        createNewspapers(libraryService)
        createDisks(libraryService)

        val items = mutableListOf<BasicLibraryElement>()

        items.addAll(LibraryRepository.getBooksInLibrary())
        items.addAll(LibraryRepository.getNewspapersInLibrary())
        items.addAll(LibraryRepository.getDisksInLibrary())

        viewModel.updateElements(items)
    }

    private fun resultHandler(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val name = result.data?.getStringExtra(SELECTED_NAME)?.trim()
                val id = result.data?.getIntExtra(SELECTED_ID, DEFAULT_ID) ?: DEFAULT_ID
                // Я решил сделать через IMAGE, так как на данный момент их логика совпадает
                val elementType = result.data?.getIntExtra(TYPE_KEY, DEFAULT_IMAGE) ?: DEFAULT_IMAGE

                if (name != null && id != DEFAULT_ID) {
                    when (elementType) {
                        BOOK_IMAGE -> {
                            val newBook = LibraryElementFactory.createBook(name, id)
                            viewModel.updateElements(listOf(newBook))
                            makeText(
                                this@MainActivity,
                                "Добавлена книга",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        NEWSPAPER_IMAGE -> {
                            val newNewspaper = LibraryElementFactory.createNewspaper(name, id)
                            viewModel.updateElements(listOf(newNewspaper))
                            makeText(
                                this@MainActivity,
                                "Добавлена газета",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        DISK_IMAGE -> {
                            val newDisk = LibraryElementFactory.createDisk(name, id)
                            viewModel.updateElements(listOf(newDisk))

                            makeText(
                                this@MainActivity,
                                "Добавлен диск",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            makeText(
                                this,
                                "Такой подборки в библиотеке нет",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    makeText(
                        this,
                        "Неверное имя или id, попробуйте ещё раз",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showAddItemBottomSheet() {
        val bottomSheet = BottomSheetForAddLibraryItemBinding.inflate(layoutInflater)

        val bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(bottomSheet.root)
            setCancelable(true)
        }

        // Устанавливаю слушателей нажатий для кнопок добавления
        bottomSheet.apply {
            addBook.setOnClickListener {
                startForResult.launch( // возвращает Result, для добавления созданного элемента через ViewModel
                    SelectedItemActivity.createIntent(
                        this@MainActivity,
                        null,
                        CREATE_TYPE,
                        BOOK_IMAGE
                    )
                )
                bottomSheetDialog.dismiss()
            }

            addNewspaper.setOnClickListener {
                startForResult.launch(
                    SelectedItemActivity.createIntent(
                        this@MainActivity,
                        null,
                        CREATE_TYPE,
                        NEWSPAPER_IMAGE
                    )
                )
                bottomSheetDialog.dismiss()
            }

            addDisk.setOnClickListener {
                startForResult.launch(
                    SelectedItemActivity.createIntent(
                        this@MainActivity,
                        null,
                        CREATE_TYPE,
                        DISK_IMAGE
                    )
                )
                bottomSheetDialog.dismiss()
            }
        }

        // Это тоже не помогло, оно всё равно вылезает несколько раз при быстром нажатии
        if (!bottomSheetDialog.isShowing && !isFinishing) {
            bottomSheetDialog.show()
        }
    }

    private fun initViewModel() {
        val factory = ViewModelFactory()

        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        viewModel.elements.observe(this) { notes ->
            libraryAdapter.submitList(notes)
        }
    }

    companion object {
        // Для списка элементов
        const val SPAN_COUNT = 2
        const val SPACES_ITEM_DECORATION_COUNT = 12
    }
}