package presentation.show_items

import presentation.show_actions.showActions
import ru.phantom.common.library_service.LibraryRepository.getItem
import ru.phantom.common.entities.library.Readable
import ru.phantom.common.entities.library.Showable

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