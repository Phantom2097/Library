package ru.phantom.library.presentation.google_books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.phantom.library.R
import ru.phantom.library.databinding.FiltersForRequestGoogleBooksBinding
import ru.phantom.library.presentation.animations.Animations.animationDisableButton
import ru.phantom.library.presentation.animations.Animations.animationEnableButton
import ru.phantom.library.presentation.main.DisplayStates
import ru.phantom.library.presentation.main.MainViewModel

class SettingQueryFilters : Fragment(R.layout.filters_for_request_google_books) {

    private var _binding: FiltersForRequestGoogleBooksBinding? = null
    val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FiltersForRequestGoogleBooksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        redefineBackButton()

        subscribeViewModel()
        initUI()
        initButton()

        subscribeToInput()
    }

    private fun initUI() {
        val (author, title) = viewModel.requestDescription.value

        binding.apply {
            authorTextFieldGoogleBooks.setText(author)
            titleTextFieldGoogleBooks.setText(title)
        }
    }

    private fun initButton() {
        val getBooksButton = binding.buttonStartSearchGoogleBooks
        val clearFieldsButton = binding.buttonClearFiltersGoogleBooks

        getBooksButton.setOnClickListener {
            viewModel.clearList()
            viewModel.getGoogleBooks(buildQuery())
            navController.navigate(R.id.allLibraryItemsList)
        }

        clearFieldsButton.setOnClickListener {
            viewModel.clearRequestDescription()
            binding.apply {
                authorTextFieldGoogleBooks.setText(EMPTY_TEXT)
                titleTextFieldGoogleBooks.setText(EMPTY_TEXT)
            }
        }
    }

    private fun subscribeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.requestDescription.collect { (author, title) ->
                    binding.buttonStartSearchGoogleBooks.apply {
                        if (author.length < LIMIT_LENGTH && title.length < LIMIT_LENGTH) {
                            animationDisableButton()
                        } else {
                            animationEnableButton()
                        }

                    }
                    binding.buttonClearFiltersGoogleBooks.apply {
                        if (author.isBlank() && title.isBlank()) {
                            animationDisableButton()
                        } else {
                            animationEnableButton()
                        }
                    }
                }
            }
        }
    }


    private fun subscribeToInput() {
        binding.apply {

            authorTextFieldGoogleBooks.doAfterTextChanged { text ->
                viewModel.updateAuthor(text.toString())
            }

            titleTextFieldGoogleBooks.doAfterTextChanged { text ->
                viewModel.updateTitle(text.toString())
            }
        }
    }

    /**
     * Функция для составления запроса к API
     */
    private fun buildQuery(): String {

        val (author, title) = viewModel.requestDescription.value

        val parts = mutableListOf<String>()
        if (title.isNotBlank()) parts += "intitle:$title"
        if (author.isNotBlank()) parts += "inauthor:$author"
        return parts.joinToString("+")
    }

    /**
     * Добавляет действия выполняемые системной кнопкой назад
     */
    private fun redefineBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this@SettingQueryFilters,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.changeMainScreen(DisplayStates.MY_LIBRARY)
                    viewModel.loadCurrent()
                    // ещё менять состояние кнопок
                    navController.popBackStack(R.id.allLibraryItemsList, false)
                }
            }
        )
    }


    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

    private companion object {
        private const val EMPTY_TEXT = ""
        private const val LIMIT_LENGTH = 3
    }
}