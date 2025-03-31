package ru.phantom.library.domain.main_recycler.view_holder

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.Itemable
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.DiskImpl
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
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
            is Newspaper -> R.drawable.twotone_newspaper_24
            else -> R.drawable.baseline_question_mark_24
        }

        binding.itemIconInCards.setImageResource(icon)
    }

    fun bindAvailability(availability: Boolean) = with(binding) {
        if (availability) {
            itemNameInCards.alpha = ENABLED_ALPHA
            itemIdInCards.alpha = ENABLED_ALPHA
            libraryItemsCards.elevation = ENABLED_ELEVATION
        } else {
            itemNameInCards.alpha = DISABLED_ALPHA
            itemIdInCards.alpha = DISABLED_ALPHA
            libraryItemsCards.elevation = DISABLED_ELEVATION
        }
    }

    companion object LibraryViewHolderConsts {
        const val ENABLED_ALPHA = 1.0f
        const val DISABLED_ALPHA = 0.3f

        const val ENABLED_ELEVATION = 10.0f
        const val DISABLED_ELEVATION = 1.0f
    }
}
