package ru.phantom.library.domain.main_recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.Itemable
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.DiskImpl
import ru.phantom.library.data.entites.library.items.newspaper.NewspaperImpl
import ru.phantom.library.databinding.LibraryItemRecyclerForMainBinding

class LibraryItemsAdapter : RecyclerView.Adapter<LibraryItemsAdapter.LibraryViewHolder>() {

    var data: List<Itemable> = emptyList()

    class LibraryViewHolder(private val binding: LibraryItemRecyclerForMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Itemable) = with(binding) {
            itemNameInCards.text = item.getName()
            itemIdInCards.text = item.getId().toString()
            val icon = when (item) {
                is BookImpl -> R.drawable.twotone_menu_book_24
                is DiskImpl -> R.drawable.twotone_disc_full_24
                is NewspaperImpl -> R.drawable.twotone_newspaper_24
                else -> R.drawable.baseline_question_mark_24
            }
            itemIconInCards.setImageResource(icon)
            if (item.getAvailability()) {
                with(itemLibraryCard) {
                    alpha = 1.0f
                    elevation = 20.0f
                }
            } else {
                with(itemLibraryCard) {
                    alpha = 0.3f
                    elevation = 1.0f
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val binding = LibraryItemRecyclerForMainBinding.inflate(
            LayoutInflater.from(parent.context)
        )
        return LibraryViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(data[position])
//        when (val item = data[position]) {
////            is BookImpl -> (holder as BookLibraryViewHolder).bind(item)
////            is NewspaperImpl -> {}
////            is DiskImpl -> {}
//            else -> holder.bind(item)
//        }
    }

    fun addItems(items: List<Itemable>) {
        data = items
//        notifyDataSetChanged()
    }
}