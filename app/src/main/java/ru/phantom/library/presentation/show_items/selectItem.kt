package presentation.show_items

import data.repository.LibraryRepository.getItem
import ru.phantom.library.data.entites.library.Readable
import ru.phantom.library.data.entites.library.Showable
import presentation.show_actions.showActions

inline fun <reified LibraryType> selectItem(
    getItems: () -> List<LibraryType>
) where LibraryType : Readable,
        LibraryType : Showable
{
    while (true) {
        val items = getItems()
        items.apply {
            // Отображение всех элементов из подборки
            showItems()
            // Получение номера объекта из подборки
            val num = getItemNumber() ?: return

            // Получаем объект из нужной подборки
            val item = getItem(items, num) ?: return@apply

            // Переходим к выбору действий с объектом. True возвращает к начальной странице библиотеки
            val exit = showActions(item)
            if (exit) return
        }
    }
}