package presentation.show_actions

import ru.phantom.library.data.entites.library.Readable
import ru.phantom.library.data.entites.library.Showable
import presentation.colors.Colors.ANSI_CYAN
import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW
import presentation.show_actions.SelectActionConsts.INVALID_NUMBER

fun <T> selectAction(currentItem: T): Boolean? where T : Showable, T : Readable {
    currentItem.apply {
        when (readlnOrNull()?.toIntOrNull() ?: INVALID_NUMBER) {
            1 -> {
                println(takeToHome())
                return false
            }
            2 -> {
                println(readInTheReadingRoom())
                return false
            }
            3 -> println(fullInformation())
            4 -> {
                println(returnInLibrary())
                return false
            }
            5 -> println(briefInformation() + "\n")
            6 -> return false
            7 -> return true
            else -> {
                println(ANSI_YELLOW + "Неизвестная команда\n" + ANSI_CYAN + "\tПопробуйте ещё раз\n" + ANSI_RESET)
                return@apply
            }
        }
    }
    return null
}

private object SelectActionConsts {
    const val INVALID_NUMBER = -1
}