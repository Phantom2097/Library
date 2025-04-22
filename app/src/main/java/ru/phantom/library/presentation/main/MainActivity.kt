package ru.phantom.library.presentation.main

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.CREATE_TYPE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_TYPE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.states.DetailState
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail

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

        initUi()
        initNavigation()

        startScreenInitialise()
        initListenerViewModel()

        initStartItemsList(savedInstanceState)
    }

    private fun initStartItemsList(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            viewModel.updateElements(emptyList())
        }
    }

    /**
     * Функция для отображения DetailFragment в случае, если до поворота экрана, он ещё был открыт
     */
    private fun startScreenInitialise() {
        val state = viewModel.detailState.value
        if (state is LoadingStateToDetail.Loading || (state as? LoadingStateToDetail.Data)?.data?.uiType != DEFAULT_TYPE) {
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
        viewModel.detailState.collect { state ->
            if (state is LoadingStateToDetail.Loading) {
                toDetail()
            }
        }
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