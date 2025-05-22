package ru.phantom.library.presentation.main

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import ru.phantom.library.R
import ru.phantom.library.databinding.ActivityMainBinding
import ru.phantom.library.databinding.BottomSheetForAddLibraryItemBinding
import ru.phantom.library.di.AppComponentProvider
import ru.phantom.library.presentation.animations.Animations.animationDisableButton
import ru.phantom.library.presentation.animations.Animations.animationEnableButton
import ru.phantom.library.presentation.animations.Animations.animationForAddButton
import ru.phantom.library.presentation.animations.Animations.textChange
import ru.phantom.library.presentation.main.DisplayStates.GOOGLE_BOOKS
import ru.phantom.library.presentation.main.DisplayStates.MY_LIBRARY
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.CREATE_TYPE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_TYPE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.states.DetailState
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail.Data
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail.Loading
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MainViewModel>(
        factoryProducer = { viewModelFactory }
    )

    private lateinit var navController: NavController
    private lateinit var landController: NavController

    private val isLandscape get() = resources.configuration.orientation == ORIENTATION_LANDSCAPE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val component = (applicationContext as AppComponentProvider).getAppComponent()
        component.inject(this)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUi()
        initNavigation()

        startScreenInitialise()
        initListenerViewModel()
    }

    /**
     * Функция для отображения DetailFragment в случае, если до поворота экрана, он ещё был открыт
     */
    private fun startScreenInitialise() {
        val state = viewModel.detailState.value
        if (state is Loading || (state as? Data)?.data?.uiType != DEFAULT_TYPE) {
            toDetail()
        }
    }

    /**
     * Функция инициализирует floating Button
     */
    private fun initUi() {
        val addButton = binding.mainAddButton

        addButton.setOnClickListener {
            showAddItemBottomSheet()
        }

        val libraryButton = binding.showLibraryButton

        val googleBooksButton = binding.showGoogleBooksButton

        googleBooksButton.apply {
            setOnClickListener {
                viewModel.changeMainScreen(GOOGLE_BOOKS)
                navController.popBackStack(R.id.allLibraryItemsList, false)
                navController.navigate(R.id.action_allLibraryItemsList_to_booksListFromGoogle)
            }
        }

        libraryButton.apply {
            setOnClickListener {
                viewModel.apply {
                    changeMainScreen(MY_LIBRARY)
                    loadCurrent()
                }
                navController.popBackStack(R.id.allLibraryItemsList, false)
            }
        }
    }

    /**
     * Функция определяет контроллеры навигации в зависимости от конфигурации
     */
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

    /**
     * Осуществляет навигацию к DetailFragment и передаёт состояние соответствующее режиму
     * создания элементов
     * @param elementType принимает тип элемента (Книгу, Газету или Диск)
     * @param dialog принимает BottomSheetDialog
     */
    private fun navigateToCreate(elementType: Int, dialog: BottomSheetDialog) {

        viewModel.setDetailState(
            DetailState(uiType = CREATE_TYPE, image = elementType)
        )
        viewModel.updateType(elementType)

        toDetail()

        dialog.dismiss()
    }

    /**
     * Добавляет слушателя изменений данных viewModel
     * @see MainViewModel
     */
    private fun initListenerViewModel() = lifecycleScope.launch {
        launch {
            viewModel.detailState.collect { state ->
                if (state is Loading) {
                    Log.d("uitype", "Внутри активити")
                    toDetail()
                }
            }
        }

        launch {
            viewModel.screenModeState.collect { mode ->
                val libraryButton = binding.showLibraryButton
                val googleBooksButton = binding.showGoogleBooksButton
                when (mode) {
                    MY_LIBRARY -> {
                        changeMainScreenMode(
                            googleBooksButton,
                            libraryButton,
                            false,
                            getString(R.string.currentPositionMyLibraryText)
                        )
                    }

                    GOOGLE_BOOKS -> {
                        changeMainScreenMode(
                            libraryButton,
                            googleBooksButton,
                            true,
                            getString(R.string.currentPositionGoogleBooksText)
                        )
                    }
                }
            }
        }
    }

    private fun changeMainScreenMode(
        enableButton: Button,
        disableButton: Button,
        floatingButtonState: Boolean,
        mode: String
    ) {
        enableButton.animationEnableButton()
        disableButton.animationDisableButton()

        binding.mainAddButton.animationForAddButton(floatingButtonState)
        binding.currentListPage.textChange(mode)
    }


    /**
     * Осуществляет переход на DetailFragment учитывая текущую конфигурацию и открыт ли
     * на данный момент фрагмент или нет
     *
     * @see ru.phantom.library.presentation.selected_item.DetailFragment
     */
    private fun toDetail() {
        if (isLandscape) {
            Log.d("orientationDebug", "поворот в ландшафт")
            navController.popBackStack(R.id.allLibraryItemsList, false)

            landController.popBackStack(R.id.emptyFragment, false)

            landController.navigate(R.id.action_emptyFragment_to_detailFragment)
        } else {
            Log.d("orientationDebug", "поворот в портрет")

            navController.popBackStack(R.id.allLibraryItemsList, false)

            navController.navigate(R.id.action_to_detail)
        }
    }
}