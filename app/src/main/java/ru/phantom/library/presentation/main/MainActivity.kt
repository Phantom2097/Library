package ru.phantom.library.presentation.main

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
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
    private lateinit var landController: NavController

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

        initUiAndVariables()
        initNavigation()

        if (isLandscape) {
            if (viewModel.detailState.value?.uiType != DEFAULT_TYPE) {
                navController.popBackStack()
                landController.navigate(R.id.detailFragment)
            } else {
                landController.navigate(R.id.emptyFragment)
            }
        } else {
            toDetail()
        }

        // инициализирую слушателя нажатий на элемент списка
        initListenerViewModel()
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

    private fun initUiAndVariables() {
        val addButton = binding.mainAddButton

        addButton.setOnClickListener {
            showAddItemBottomSheet()
        }
    }

    private fun initNavigation() {
        val navHost =
            supportFragmentManager
                .findFragmentById(R.id.mainListContainer)
                    as NavHostFragment

        navController = navHost.findNavController()

        if (isLandscape) {
            val landNavHost =
                supportFragmentManager
                    .findFragmentById(R.id.mainRightContainer)
                        as NavHostFragment

            landController = landNavHost.findNavController()

            val detailGraph = landController
                .navInflater
                .inflate(R.navigation.nav_graph)
                .apply { setStartDestination(R.id.emptyFragment) }

            landController.graph = detailGraph
        }
    }

    private fun showAddItemBottomSheet() {
        val bottomSheet = BottomSheetForAddLibraryItemBinding.inflate(layoutInflater)

        val bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(bottomSheet.root)
            setCancelable(true)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        setupBottomSheetDialogClickListener(bottomSheet, bottomSheetDialog)

        bottomSheetDialog.show()
    }

    /**
     * Функция устанавливает слушателей нажатий для кнопок диалога
     */
    private fun setupBottomSheetDialogClickListener(
        bottomSheet: BottomSheetForAddLibraryItemBinding,
        bottomSheetDialog: BottomSheetDialog
    ) {
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
    }

    private fun navigateToCreate(elementType: Int, dialog: BottomSheetDialog) {
        viewModel.setDetailState(
            DetailState(uiType = CREATE_TYPE, image = elementType)
        )

        toDetail()

        dialog.dismiss()
    }

    private fun initListenerViewModel() {
        viewModel.itemClickEvent.observe(this) { element ->
            Log.d("CLICKED", "Дошло до слушателя")
            element?.let {
                changeDetailState(element)

                toDetail()

                viewModel.reloadListener()
            }
        }

        viewModel.detailState.observe(this) {
            if (isLandscape && viewModel.detailState.value?.uiType== DEFAULT_TYPE) {
                landController.popBackStack()
            }
        }
    }

    private fun toDetail() {
        if (isLandscape) {
            landController.navigate(R.id.detailFragment)
        } else {
            if (navController.currentDestination?.label == getString(R.string.detail_screen)) {
                navController.popBackStack(R.id.detailFragment, true)
            }
            navController.navigate(R.id.action_to_detail)
        }
    }
}