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
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.phantom.library.R
import ru.phantom.library.databinding.FiltersForRequestGoogleBooksBinding
import ru.phantom.library.presentation.main.MainViewModel

class BooksListFromGoogle : Fragment(R.layout.filters_for_request_google_books) {

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
        initButton()

        subscribeToInput()
    }

    private fun initButton() {
        val getBooksButton = binding.buttonStartSearchGoogleBooks

        getBooksButton.setOnClickListener {
            viewModel.getGoogleBooks(buildQuery())
            viewModel.clearRequestDescription()
            navController.popBackStack()
        }
    }

    private fun subscribeViewModel() {

        viewModel.viewModelScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.requestDescription.collect { (author, title) ->
                    binding.buttonStartSearchGoogleBooks.apply {
                        if (author.length < LIMIT_LENGTH && title.length < LIMIT_LENGTH) {
                            isClickable = false
                            alpha = DISABLE_ALPHA
                        } else {
                            isClickable = true
                            alpha = UNABLE_ALPHA
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
            this@BooksListFromGoogle,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // ещё менять состояние кнопок
                    navController.popBackStack()
                }
            }
        )
    }


    override fun onDetach() {
        super.onDetach()
        _binding = null
    }

    private companion object {
        private const val DISABLE_ALPHA = 0.3f
        private const val UNABLE_ALPHA = 1.0f

        private const val LIMIT_LENGTH = 3
    }
}