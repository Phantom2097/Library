package ru.phantom.library.domain.main_recycler.view_holder

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.Itemable
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.DiskImpl
import ru.phantom.library.data.entites.library.items.newspaper.NewspaperImpl
import ru.phantom.library.databinding.LibraryItemRecyclerForMainBinding

class LibraryViewHolder(private val binding: LibraryItemRecyclerForMainBinding) :
    ViewHolder(binding.root) {

    fun bind(item: Itemable) {

        // Название
        bindName(item.getName())

        // Id
        bindId(item.getId())

        // Картинка
        bindImage(item)

        // Видимость (Доступность)
        bindAvailability(item.getAvailability())
    }

    private fun bindName(name: String) = with(binding) {
        itemNameInCards.text = name
    }

    private fun bindId(id: Int) = with(binding) {
        itemIdInCards.text = id.toString()
    }

    private fun bindImage(item: Itemable) {
        val icon = when (item) {
            is BookImpl -> R.drawable.twotone_menu_book_24
            is DiskImpl -> R.drawable.twotone_album_24
            is NewspaperImpl -> R.drawable.twotone_newspaper_24
            else -> R.drawable.baseline_question_mark_24
        }

        binding.itemIconInCards.setImageResource(icon)
    }

    fun bindAvailability(availability: Boolean) = with(binding) {
        if (availability) {
//            libraryItemsCardsLayout.alpha = 1.0f
            itemNameInCards.alpha = 1.0f
            itemIdInCards.alpha = 1.0f
            libraryItemsCards.elevation = 10.0f
        } else {
//            libraryItemsCardsLayout.alpha = 0.3f
            itemNameInCards.alpha = 0.3f
            itemIdInCards.alpha = 0.3f
            libraryItemsCards.elevation = 1.0f
        }
    }
}
