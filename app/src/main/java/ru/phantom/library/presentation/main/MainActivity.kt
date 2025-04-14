package ru.phantom.library.presentation.main

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.BasicLibraryElement
import ru.phantom.library.data.entites.library.items.book.Book
import ru.phantom.library.data.entites.library.items.disk.Disk
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
import ru.phantom.library.databinding.ActivityMainBinding
import ru.phantom.library.databinding.BottomSheetForAddLibraryItemBinding
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.CREATE_TYPE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_TYPE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.SHOW_TYPE
import ru.phantom.library.presentation.selected_item.DetailState

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<MainViewModel>()

    private lateinit var navController: NavController
//    private lateinit var detailNavController: NavController

    private val isLandscape get() = resources.configuration.orientation == ORIENTATION_LANDSCAPE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupNavigation()

        // Делаю то, что связано с UI
        initUi()
//        setupNavigation()

        // инициализирую слушателя нажатий на элемент списка
        initListenerViewModel()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainListContainer) as NavHostFragment
        navController = navHostFragment.navController
    }

    fun changeDetailState(element: BasicLibraryElement? = null) {
        element?.let { element ->
            val image = when (element) {
                is Book -> BOOK_IMAGE
                is Newspaper -> NEWSPAPER_IMAGE
                is Disk -> DISK_IMAGE
                else -> DEFAULT_IMAGE
            }
            viewModel.setDetailState(
                DetailState(
                    uiType = SHOW_TYPE,
                    name = element.item.name,
                    id = element.item.id,
                    image = image,
                    description = element.fullInformation()
                )
            )
        }
    }

    private fun initUi() {
        val addButton = binding.mainAddButton

        addButton.setOnClickListener {
            showAddItemBottomSheet()
        }
    }

    private fun showAddItemBottomSheet() {
        val bottomSheet = BottomSheetForAddLibraryItemBinding.inflate(layoutInflater)

        val bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(bottomSheet.root)
            setCancelable(true)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheet.apply {
            addBook.setOnClickListener {
                navigateToCreate(BOOK_IMAGE, bottomSheetDialog)
            }
            addNewspaper.setOnClickListener {
                navigateToCreate(NEWSPAPER_IMAGE, bottomSheetDialog)
            }
            addDisk.setOnClickListener {
                navigateToCreate(DISK_IMAGE, bottomSheetDialog)
            }
        }

        bottomSheetDialog.show()
    }

    private fun navigateToCreate(elementType: Int, dialog: BottomSheetDialog) {
        viewModel.setDetailState(
            DetailState(uiType = CREATE_TYPE, image = elementType)
        )

        navigateToDetail()

        dialog.dismiss()
    }

    private fun initListenerViewModel() {
        viewModel.itemClickEvent.observe(this) { element ->
            Log.d("CLICKED", "Дошло до слушателя")
            element?.let {
                changeDetailState(element)
                navigateToDetail()

                viewModel.reloadListener()
            }
        }

        viewModel.detailState.observe(this) { state ->
            if (state.uiType == DEFAULT_TYPE) {
                supportFragmentManager.popBackStack(DETAIL_FRAGMENT_TAG, POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    private fun navigateToDetail() {
        if (isLandscape) {
            val navHostRight =
                supportFragmentManager.findFragmentById(R.id.mainRightContainer) as NavHostFragment
            val navControllerRight = navHostRight.navController
            navControllerRight.popBackStack()
            navControllerRight.navigate(R.id.detailFragment)
        } else {
            navController.navigate(
                R.id.action_to_detail
            )
        }
    }

    companion object {
        // Для списка элементов
//        const val LIST_FRAGMENT_TAG = "listFragmentTag"
        const val DETAIL_FRAGMENT_TAG = "detailFragmentTag"
    }
}