package presentation.show_items

import ru.phantom.library.data.entites.library.Showable
import presentation.colors.Colors.ANSI_BLUE
import presentation.colors.Colors.ANSI_RESET
import presentation.show_items.ShowItemsConsts.INC_FOR_SHOW

fun List<Showable>.showItems() {
    forEachIndexed { i, item ->
        val message = buildString {
            append(ANSI_BLUE)
            append("\t${i + INC_FOR_SHOW} - ")
            append(ANSI_RESET)
            append(item.briefInformation())
        }

        println(message)
    }
}

private object ShowItemsConsts {
    const val INC_FOR_SHOW = 1
}