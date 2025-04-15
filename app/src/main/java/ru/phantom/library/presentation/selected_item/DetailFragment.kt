package ru.phantom.library.presentation.selected_item

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.databinding.DetailInformationScreenBinding
import ru.phantom.library.presentation.main.MainViewModel

class DetailFragment : Fragment(R.layout.detail_information_screen) {

    private var _binding: DetailInformationScreenBinding? = null
    val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()
    private val isLandscape get() = resources.configuration.orientation == ORIENTATION_LANDSCAPE

    private lateinit var navController: NavController

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

        initNavController()

        val element = viewModel.detailState.value
        when (element?.uiType) {
            SHOW_TYPE -> onlyShow(element)
            CREATE_TYPE -> showCreateType()
            else -> showEmpty()
        }

        inputObserve()
    }

    // Заглушка, чтобы было пустое место на экране
    private fun showEmpty() {
        binding.libraryItemsCards.alpha = 0.0f
    }

    private fun onlyShow(element: DetailState) {
        binding.apply {
            libraryItemsCards.alpha = 1.0f
            selectedItemName.setText(element.name)
            selectedItemId.setText(element.id.toString())
            selectedItemIcon.setImageResource(element.image)
            element.description?.let {
                selectedItemDescription.text = it
            }

            saveElementButton.apply {
                text = context.getString(R.string.saveButtonShowType)
                setOnClickListener {
                    backStackSimulator()
                }
            }
        }
    }

    private fun showCreateType() {
        val element = viewModel.detailState.value
        val image = element!!.image

        with(binding) {
            libraryItemsCards.alpha = 1.0f
            selectedItemName.apply {
                focusable = View.FOCUSABLE
                isFocusableInTouchMode = true
                setText(viewModel.detailState.value?.name.orEmpty())
            }

            selectedItemId.apply {
                focusable = View.FOCUSABLE
                isFocusableInTouchMode = true
                viewModel.detailState.value?.let {
                    if (it.id != DEFAULT_ID) {
                        this@apply.setText(it.id.toString())
                    }
                }
            }

            selectedItemIcon.setImageResource(image)

            saveElementButton.apply {
                text = context.getString(R.string.saveButtonCreateType)

                setOnClickListener {
                    val newName = selectedItemName.text?.toString()?.trim()
                    val newId = selectedItemId.text.toString().toIntOrNull() ?: -1

                    val textToast = when {
                        newName.isNullOrBlank() && newId == -1 -> context.getString(R.string.noNameAndIdCreateType)
                        newName.isNullOrBlank() -> context.getString(R.string.noNameCreateType)
                        newId == -1 -> context.getString(R.string.noIdCreateType)
                        else -> {
                            viewModel.addNewElement(LibraryItem(newName, newId), image)

                            backStackSimulator()

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

    private fun inputObserve() {
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

    // Этот метод позволяет притвориться, что всё хорошо
    private fun backStackSimulator() {
        viewModel.setDetailState(DetailState())
        if (isLandscape) {
            showEmpty()
        } else {
            findNavController().navigateUp()
        }
    }

    override fun onResume() {
        super.onResume()

        if (isLandscape) {
            navController.navigateUp()
        }
    }

    private fun initNavController() {
        navController = Navigation.findNavController(
            requireActivity(),
            R.id.mainListContainer
        )
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