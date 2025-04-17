package ru.phantom.library.presentation.selected_item

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.databinding.DetailInformationScreenBinding
import ru.phantom.library.presentation.main.MainViewModel

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

        viewModel.detailState.value?.let { changeUiType(it) }

        inputTextObserve()

        redefineBackButton()
    }

    private fun changeUiType(element: DetailState) {
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
        val element = viewModel.detailState.value
        val image = element!!.image

        with(binding) {
            selectedItemName.apply {
                setEditState(this)

                setText(viewModel.detailState.value?.name.orEmpty())
            }

            selectedItemId.apply {
                setEditState(this)

                viewModel.detailState.value?.let { state ->
                    if (state.id != DEFAULT_ID) {
                        this@apply.setText(state.id.toString())
                    }
                }
            }

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
                viewModel.detailState.value?.let {
                    viewModel.setDetailState(it.copy(name = newText?.toString() ?: DEFAULT_NAME))
                }
            }

            selectedItemId.doAfterTextChanged { newId ->
                viewModel.detailState.value?.let {
                    viewModel.setDetailState(
                        it.copy(
                            id = newId?.toString()?.toIntOrNull() ?: DEFAULT_ID
                        )
                    )
                }
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