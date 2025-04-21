package ru.phantom.library.presentation.selected_item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.phantom.library.R
import ru.phantom.library.databinding.ErrorDetailFragmentBinding
import ru.phantom.library.presentation.main.MainViewModel


class ErrorFragment : Fragment(R.layout.error_detail_fragment) {

    private var _binding : ErrorDetailFragmentBinding? = null
    val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ErrorDetailFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.errorButton.setOnClickListener {
            viewModel.setDetailState()
        }
        redefineBackButton()
    }
    /**
     * Добавляет действия выполняемые системной кнопкой назад
     */
    private fun redefineBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(
            this@ErrorFragment,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.setDetailState()
                }
            }
        )
    }
}