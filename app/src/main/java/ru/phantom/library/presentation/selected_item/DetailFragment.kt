package ru.phantom.library.presentation.selected_item

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.databinding.DetailInformationScreenBinding
import ru.phantom.library.presentation.main.MainViewModel
import ru.phantom.library.presentation.selected_item.states.DetailState
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail

class DetailFragment : Fragment(R.layout.detail_information_screen) {

    private var _binding: DetailInformationScreenBinding? = null
    val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()
    private val isLandscape get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailInformationScreenBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        redefineBackButton()

        initDetailStateScreen()
    }

    /**
     * Обновляет состояние DetailFragment в зависимости от поступающего LoadingStateToDetail
     *
     * @see LoadingStateToDetail
     */
    private fun initDetailStateScreen() {
        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {

                viewModel.detailState.collect { state ->
                    if (!isAdded) return@collect

                    when (state) {
                        is LoadingStateToDetail.Loading -> {
                            Log.d("uitype", "Сейчас должен показываться загрузочный экран")
                            binding.apply {
                                detailFragmentShimmer.isVisible = true
                                libraryItemsCards.isGone = true
                            }
                        }

                        is LoadingStateToDetail.Data -> {
                            Log.d("uitype", "сейчас должны отобразиться данные")
                            binding.apply {
                                detailFragmentShimmer.isGone = true
                                libraryItemsCards.isVisible = true
                            }
                            changeUiType(state.data)
                        }

                        is LoadingStateToDetail.Error -> {
                            findNavController().navigate(R.id.action_detailFragment_to_errorFragment)
                        }
                    }
                }
            }
        }
    }

    private fun changeUiType(element: DetailState) {
        Log.d("uitype", "в функции changeUitype DetailFragment type: ${element.uiType}")
        when (element.uiType) {
            SHOW_TYPE -> onlyShow(element)
            CREATE_TYPE -> showCreateType()
            else -> backButton()
        }
    }

    /**
     * Добавляет действия выполняемые системной кнопкой назад
     */
    private fun redefineBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this@DetailFragment,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    backButton()
                }
            }
        )
    }

    private fun onlyShow(element: DetailState) {
        binding.apply {
            selectedItemName.setText(element.name)
            selectedItemId.setText(element.id.toString())
            selectedItemIcon.setImageResource(element.image)
            element.description?.let {
                selectedItemDescription.text = it
            }

            saveElementButton.apply {
                text = context.getString(R.string.saveButtonShowType)
                setOnClickListener {
                    backButton()
                }
            }
        }
    }

    private fun showCreateType() {

        val element = viewModel.createState.value
        val image = element.itemType

        with(binding) {
            selectedItemName.apply {
                setEditState(this)

                setText(element.name ?: DEFAULT_NAME)
            }

            selectedItemId.apply {
                setEditState(this)

                if (element.id != null && element.id != DEFAULT_ID) {
                    this@apply.setText(element.id.toString())
                }
            }

            inputTextObserve()

            selectedItemIcon.setImageResource(image)

            saveElementButton.apply {
                text = context.getString(R.string.saveButtonCreateType)

                setOnClickListener {
                    val newName = selectedItemName.text?.toString()?.trim()
                    val newId = selectedItemId.text.toString().toIntOrNull() ?: DEFAULT_ID

                    val textToast = when {
                        newName.isNullOrBlank() && newId == DEFAULT_ID -> context.getString(R.string.noNameAndIdCreateType)
                        newName.isNullOrBlank() -> context.getString(R.string.noNameCreateType)
                        newId == DEFAULT_ID -> context.getString(R.string.noIdCreateType)
                        else -> {
                            viewModel.addNewElement(LibraryItem(newName, newId), image)
                            viewModel.clearCreate()

                            backButton()

                            null
                        }
                    }
                    textToast?.let { text ->
                        makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setEditState(button: EditText) = with(button) {
        focusable = View.FOCUSABLE
        isFocusableInTouchMode = true
    }

    /**
     * Функция добавляет слушателей изменения текста
     */
    private fun inputTextObserve() {

        binding.apply {

            selectedItemName.doAfterTextChanged { newText ->
                viewModel.updateName(newText.toString())
            }

            selectedItemId.doAfterTextChanged { newId ->
                viewModel.updateId(newId?.toString()?.toIntOrNull() ?: DEFAULT_ID)
            }
        }
    }

    /**
     * Действие выполняемое по нажатию кнопки назад
     */
    private fun backButton() {
        viewModel.setDetailState()

        if (isLandscape) {
            findNavController().popBackStack(R.id.emptyFragment, false)
        } else {
            findNavController().popBackStack(R.id.allLibraryItemsList, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val DEFAULT_NAME = ""
        const val DEFAULT_ID = -1

        // Типы отображаемого UI
        const val DEFAULT_TYPE = -1
        const val CREATE_TYPE = 0
        const val SHOW_TYPE = 1

        val BOOK_IMAGE = R.drawable.twotone_menu_book_24
        val NEWSPAPER_IMAGE = R.drawable.twotone_newspaper_24
        val DISK_IMAGE = R.drawable.twotone_album_24
        val DEFAULT_IMAGE = R.drawable.baseline_question_mark_24
    }
}