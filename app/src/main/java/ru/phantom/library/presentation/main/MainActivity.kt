package ru.phantom.library.presentation.main

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.BasicLibraryElement
import ru.phantom.library.data.entites.library.items.book.Book
import ru.phantom.library.data.entites.library.items.disk.Disk
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
import ru.phantom.library.databinding.ActivityMainBinding
import ru.phantom.library.databinding.BottomSheetForAddLibraryItemBinding
import ru.phantom.library.presentation.selected_item.DetailFragment
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.BOOK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.CREATE_TYPE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DEFAULT_TYPE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.DISK_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.NEWSPAPER_IMAGE
import ru.phantom.library.presentation.selected_item.DetailFragment.Companion.SHOW_TYPE

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Делаю то, что связано с UI
        initUi()

        // инициализирую слушателя нажатий на элемент списка
        initListenerViewModel()

        if (savedInstanceState == null) {
            openListFragment()
        } else {
            openDetailFragment()
        }
    }

    private fun openListFragment() {
        val listFragment: Fragment = AllLibraryItemsList.createListFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(binding.mainListContainer.id, listFragment, LIST_FRAGMENT_TAG)
            .commit()
    }

    fun openDetailFragment(element: BasicLibraryElement? = null) {
        val currentOrientation = resources.configuration.orientation

        val container = if (currentOrientation == ORIENTATION_LANDSCAPE) {
            Log.d("ORIENTATION1", "isLandscape")
            binding.mainRightContainer!!.id
        } else {
            Log.d("ORIENTATION1", "isPortrait")
            binding.mainListContainer.id
        }

        val detailFragment = supportFragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG)

        Log.d("ORIENTATION1", "before detail $currentOrientation")
        detailFragment?.let { fragment ->
            supportFragmentManager.apply {
                popBackStack(DETAIL_FRAGMENT_TAG, POP_BACK_STACK_INCLUSIVE)

                beginTransaction()
                    .remove(detailFragment).commit()
            }
        }

        element?.let { element ->
            val image = when (element) {
                is Book -> BOOK_IMAGE
                is Newspaper -> NEWSPAPER_IMAGE
                is Disk -> DISK_IMAGE
                else -> DEFAULT_IMAGE
            }
            viewModel.setDetailState(
                MainViewModel.DetailState(
                    uiType = SHOW_TYPE,
                    name = element.item.name,
                    id = element.item.id,
                    image = image,
                    description = element.fullInformation()
                )
            )
        }
        if (detailFragment != null || element != null) {
            val newDetailFragment = DetailFragment.createDetailFragment()
            supportFragmentManager
                .beginTransaction()
                .replace(container, newDetailFragment, DETAIL_FRAGMENT_TAG)
                .addToBackStack(DETAIL_FRAGMENT_TAG)
                .commit()
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

        // Устанавливаю слушателей нажатий для кнопок добавления
        bottomSheet.apply {
            // Вынес в отдельную функцию скрытие диалога и запуск активити создания элемента
            addBook.setOnClickListener { setBottomSheetButtons(BOOK_IMAGE, bottomSheetDialog) }
            addNewspaper.setOnClickListener {
                setBottomSheetButtons(NEWSPAPER_IMAGE, bottomSheetDialog)
            }
            addDisk.setOnClickListener { setBottomSheetButtons(DISK_IMAGE, bottomSheetDialog) }
        }

        bottomSheetDialog.show()
    }

    private fun initListenerViewModel() {
        viewModel.itemClickEvent.observe(this) { element ->
            Log.d("CLICKED", "Дошло до слушателя")
            element?.let {
                openDetailFragment(element)
                viewModel.reloadListener()
            }
        }

        viewModel.detailState.observe(this) { state ->
            if (state.uiType == DEFAULT_TYPE) {
                supportFragmentManager.popBackStack(DETAIL_FRAGMENT_TAG, POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    private fun setBottomSheetButtons(elementType: Int, dialog: BottomSheetDialog) {
        val fragment = DetailFragment.createDetailFragment()

        viewModel.setDetailState(
            MainViewModel.DetailState(uiType = CREATE_TYPE, image = elementType)
        )

        supportFragmentManager
            .beginTransaction().apply {
                // Если есть такой фрагмент в бэкстеке, то вроде удаляю все вхождения (вроде одинаковое поведение у нуля и POP_BACK_STACK_INCLUSIVE)
                // POP_BACK_STACK_INCLUSIVE удаляет все, 0 видимо не удаляет
                supportFragmentManager.popBackStack(
                    DETAIL_FRAGMENT_TAG,
                    POP_BACK_STACK_INCLUSIVE
                )
                addToBackStack(DETAIL_FRAGMENT_TAG)
                // Здесь задаю container
                val container = binding.mainRightContainer?.id ?: binding.mainListContainer.id
                replace(container, fragment, DETAIL_FRAGMENT_TAG)
            }
            .commit()

        dialog.dismiss()
    }

    companion object {
        // Для списка элементов
        const val LIST_FRAGMENT_TAG = "listFragmentTag"
        const val DETAIL_FRAGMENT_TAG = "detailFragmentTag"
    }
}