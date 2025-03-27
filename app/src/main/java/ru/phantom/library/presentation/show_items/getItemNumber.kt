package presentation.show_items

import presentation.show_items.GetNumberConsts.DECREMENT_INDEX
import presentation.show_items.GetNumberConsts.INVALID_INDEX

fun getItemNumber(): Int? {
    println("\nВведите номер для дальнейших действий\nНажмите Enter для выхода")

    return (readlnOrNull()?.run {
        if (isBlank()) return null
        else toIntOrNull()
    } ?: INVALID_INDEX) + DECREMENT_INDEX
}

private object GetNumberConsts {
    const val INVALID_INDEX = 0
    const val DECREMENT_INDEX = -1
}