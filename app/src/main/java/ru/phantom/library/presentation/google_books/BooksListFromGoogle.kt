package ru.phantom.library.presentation.google_books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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

        subscribeToInput()
    }


    private fun subscribeToInput() {
        binding.apply {

            authorTextFieldGoogleBooks.doAfterTextChanged { text ->

            }

            titleTextFieldGoogleBooks.doAfterTextChanged { text ->

            }
        }
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

    companion object {
        const val AUTHOR_KEY = "AuthorKey"
        const val TITLE_KEY = "TitleKey"
    }
}