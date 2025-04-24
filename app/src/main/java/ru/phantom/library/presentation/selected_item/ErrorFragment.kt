package ru.phantom.library.presentation.selected_item

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.phantom.library.R
import ru.phantom.library.databinding.ErrorDetailFragmentBinding
import ru.phantom.library.presentation.main.MainViewModel
import ru.phantom.library.presentation.selected_item.states.LoadingStateToDetail


class ErrorFragment : Fragment(R.layout.error_detail_fragment) {

    private var _binding : ErrorDetailFragmentBinding? = null
    val binding get() = _binding!!

    private val isLandscape get() = resources.configuration.orientation == ORIENTATION_LANDSCAPE

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

        binding.apply {

            viewModel.detailState.value.let { state ->
                errorText.text = (state as? LoadingStateToDetail.Error)?.e ?: getString(R.string.detailFragmentErrorText)
            }
            errorButton.setOnClickListener {
                viewModel.setDetailState()
                backToStart()
            }
        }

        redefineBackButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
                    backToStart()
                }
            }
        )
    }

    private fun backToStart() {
        if (!isAdded) return

        if (isLandscape) {
            findNavController().popBackStack(R.id.emptyFragment, false)
        } else {
            findNavController().popBackStack(R.id.allLibraryItemsList, false)
        }
    }
}