package presentation.show_manager

import presentation.show_manager.GetItemCountConsts.INVALID_INDEX

fun getItemCount(): Int? {

    println("Введите количество, которое Менеджер должен приобрести\n" +
            "Нажмите Enter для выхода")

    return (readlnOrNull()?.run {
        if (isBlank()) return null
        else toIntOrNull()
    } ?: INVALID_INDEX)
}

private object GetItemCountConsts {
    const val INVALID_INDEX = -1
}