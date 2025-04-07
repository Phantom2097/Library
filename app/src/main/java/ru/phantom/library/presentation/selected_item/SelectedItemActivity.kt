package ru.phantom.library.presentation.selected_item

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.phantom.library.R
import ru.phantom.library.data.entites.library.items.BasicLibraryElement
import ru.phantom.library.data.entites.library.items.book.Book
import ru.phantom.library.data.entites.library.items.disk.Disk
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
import ru.phantom.library.databinding.ActivitySelectedItemBinding

class SelectedItemActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySelectedItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (intent.hasExtra(TYPE_KEY)) {
            when (intent.getIntExtra(TYPE_KEY, DEFAULT_TYPE)) {
                CREATE_TYPE -> showCreateType()
                SHOW_TYPE -> showOnly()
                else -> showCreateType()
            }
        }
    }

    private fun showCreateType() {
        val image = intent.getIntExtra(SELECTED_ICON, DEFAULT_IMAGE)

        with(binding) {
            selectedItemName.apply {
                focusable = View.FOCUSABLE
                isFocusableInTouchMode = true
            }

            selectedItemId.apply {
                focusable = View.FOCUSABLE
                isFocusableInTouchMode = true
            }

            selectedItemIcon.setImageResource(image)

            saveElementButton.apply {
                text = "Сохранить"

                setOnClickListener {
                    val newName = selectedItemName.text.toString()
                    val newId = selectedItemId.text.toString().toIntOrNull() ?: -1

                    val resultIntent = Intent().apply {
                        putExtra(SELECTED_NAME, newName)
                        putExtra(SELECTED_ID, newId)
                        putExtra(TYPE_KEY, image)
                    }

                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
    }

    private fun showOnly() {
        val name =
            if (intent.hasExtra(SELECTED_NAME)) intent.getStringExtra(SELECTED_NAME) else null
        val id = intent.getIntExtra(SELECTED_ID, DEFAULT_ID)
//        val availability = intent.getBooleanExtra(SELECTED_AVAILABILITY, false)
        val image = intent.getIntExtra(SELECTED_ICON, DEFAULT_IMAGE)
        val description = intent.getStringExtra(SELECTED_DESCRIPTION)

        with(binding) {
            selectedItemIcon.setImageResource(image)
            selectedItemName.setText(name)
            val textForId = "Id: $id"
            selectedItemId.setText(textForId)
            selectedItemDescription.text = description

            saveElementButton.apply {
                text = "Назад"
                setOnClickListener {
                    finish()
                }
            }
        }
    }

    companion object {
        // Ключи для создания и доступа к данным в интенте
        const val SELECTED_NAME = "selectedName"
        const val SELECTED_ID = "selectedId"
        private const val SELECTED_AVAILABILITY = "selectedAvailability"
        private const val SELECTED_ICON = "selectedIcon"
        private const val SELECTED_DESCRIPTION = "selectedDescription"
        const val TYPE_KEY = "typeKey"

        const val DEFAULT_ID = -1

        // Типы отображаемого UI
        const val DEFAULT_TYPE = -1
        const val CREATE_TYPE = 0
        const val SHOW_TYPE = 1

        val BOOK_IMAGE = R.drawable.twotone_menu_book_24
        val NEWSPAPER_IMAGE = R.drawable.twotone_newspaper_24
        val DISK_IMAGE = R.drawable.twotone_album_24
        val DEFAULT_IMAGE = R.drawable.baseline_question_mark_24

        fun createIntent(
            context: Context,
            element: BasicLibraryElement?,
            type: Int = SHOW_TYPE,
            imageType: Int = DEFAULT_IMAGE
        ): Intent {

            element?.let {
                val iconType = when (element) {
                    is Book -> BOOK_IMAGE
                    is Disk -> DISK_IMAGE
                    is Newspaper -> NEWSPAPER_IMAGE
                    else -> DEFAULT_IMAGE
                }
                return Intent(context, SelectedItemActivity::class.java)
                    .putExtra(SELECTED_NAME, element.item.name)
                    .putExtra(SELECTED_ID, element.item.id)
                    .putExtra(SELECTED_AVAILABILITY, element.item.availability)
                    .putExtra(SELECTED_ICON, iconType)
                    .putExtra(SELECTED_DESCRIPTION, element.fullInformation())
                    .putExtra(TYPE_KEY, type)
            }

            return Intent(context, SelectedItemActivity::class.java)
                .putExtra(TYPE_KEY, type)
                .putExtra(SELECTED_ICON, imageType)
        }
    }
}