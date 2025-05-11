package ru.phantom.library.presentation.google_books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.phantom.library.R
import ru.phantom.library.databinding.AllLibraryItemsListBinding
import ru.phantom.library.domain.main_recycler.adapter.LibraryItemsAdapter

class ListOfReceivedBooks : Fragment(R.layout.all_library_items_list) {

    private var _binding : AllLibraryItemsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LibraryItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AllLibraryItemsListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }



    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}