package ru.phantom.library.domain.main_recycler.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.phantom.library.data.entites.library.items.Itemable
import ru.phantom.library.data.entites.library.items.book.BookImpl
import ru.phantom.library.data.entites.library.items.disk.DiskImpl
import ru.phantom.library.data.entites.library.items.newspaper.NewspaperImpl
import ru.phantom.library.databinding.LibraryItemRecyclerForMainBinding
import ru.phantom.library.domain.main_recycler.ViewTypedLibraryWrapper
import ru.phantom.library.domain.main_recycler.utils.DiffCallback
import ru.phantom.library.domain.main_recycler.view_holder.LibraryViewHolder

class LibraryItemsAdapter : RecyclerView.Adapter<LibraryViewHolder>() {

    var data: MutableList<ViewTypedLibraryWrapper> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val binding = LibraryItemRecyclerForMainBinding.inflate(
            LayoutInflater.from(parent.context)
        )
        return LibraryViewHolder(binding).apply {
            binding.root.setOnClickListener {
                changeAvailabilityClick(parent.context, adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val itemWrapper = data[position].getItemable()
        holder.bind(itemWrapper)
    }

    override fun onBindViewHolder(
        holder: LibraryViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach { payload ->
                when (payload) {
                    is DiffCallback.ItemAvailabilityChange ->
                        holder.bindAvailability(payload.changedAvailability)
                }
            }
        }
    }

    override fun getItemCount(): Int = data.size

    fun addItems(items: MutableList<Itemable>) {

        val wrapped = items.map { item ->
            when (item) {
                is BookImpl -> ViewTypedLibraryWrapper.Book(item)
                is DiskImpl -> ViewTypedLibraryWrapper.Disk(item)
                is NewspaperImpl -> ViewTypedLibraryWrapper.Newspaper(item)
                else -> throw IllegalArgumentException("Так не должно быть")
            }
        }

        updateList(data, wrapped)
    }

    fun removeItem(position: Int) {
        val newList = data.toMutableList().apply { removeAt(position) }

        Log.d("Size", "prev size = ${data.size}")
        Log.d("Size", "new size = ${newList.size}")

        updateList(data, newList)
    }

    private fun changeAvailabilityClick(context: Context, position: Int) {
        if (position !in data.indices) return

        val newItem = data[position].getItemable().apply { setAvailability() }

        // проблема сразу видна
        Log.d(
            "Availability",
            "prev availability = ${data[position].getItemable().getAvailability()}"
        )
        Log.d("Availability", "new availability = ${newItem.getAvailability()}")

        /*
        Принудительное обновление, потому что классы не data, иначе не могу использовать наследование
        Пробовал поменять наследование Newspaper на композицию и делегирование,
        Но тогда надо менять всю логику старого кода
        */
        notifyItemChanged(position, DiffCallback.ItemAvailabilityChange(newItem.getAvailability()))

        makeText(
            context,
            "Предмет c id: ${newItem.getId()} ${if (newItem.getAvailability()) "да" else "нет"}",
            LENGTH_SHORT
        ).show()
    }

    private fun updateList(
        oldList: List<ViewTypedLibraryWrapper>,
        newList: List<ViewTypedLibraryWrapper>
    ) {
        val diffUtil = DiffCallback(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        data.clear()
        data.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }

    // Callback для ItemTouchCallback, чтобы свайпать элементы
    fun getMySimpleCallback() = MySimpleCallback()

    inner class MySimpleCallback : SimpleCallback(
        NO_ACTION,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: ViewHolder,
            target: ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
            val currPosition = viewHolder.adapterPosition

            if (currPosition != RecyclerView.NO_POSITION) {
                removeItem(currPosition)
            }
        }
    }

    companion object AdapterConsts {
        const val NO_ACTION = 0
    }
}