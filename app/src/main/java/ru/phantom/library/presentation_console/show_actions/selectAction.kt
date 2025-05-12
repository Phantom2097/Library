package presentation.show_actions

import ru.phantom.common.entities.library.Readable
import ru.phantom.common.entities.library.Showable
import presentation.colors.Colors.ANSI_CYAN
import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW
import presentation.show_actions.SelectActionConsts.BRIEF_INFORMATION
import presentation.show_actions.SelectActionConsts.FULL_INFORMATION
import presentation.show_actions.SelectActionConsts.GO_BACK_PAGE
import presentation.show_actions.SelectActionConsts.GO_HOME_PAGE
import presentation.show_actions.SelectActionConsts.INVALID_NUMBER
import presentation.show_actions.SelectActionConsts.READ_IN_READING_ROOM
import presentation.show_actions.SelectActionConsts.RETURN_TO_LIBRARY
import presentation.show_actions.SelectActionConsts.TAKE_TO_HOME

fun <T> selectAction(currentItem: T): Boolean? where T : Showable, T : Readable {
    currentItem.apply {
        when (readlnOrNull()?.toIntOrNull() ?: INVALID_NUMBER) {
            TAKE_TO_HOME -> {
                println(takeToHome())
                return false
            }
            READ_IN_READING_ROOM -> {
                println(readInTheReadingRoom())
                return false
            }
            FULL_INFORMATION -> println(fullInformation())
            RETURN_TO_LIBRARY -> {
                println(returnInLibrary())
                return false
            }
            BRIEF_INFORMATION -> println(briefInformation() + "\n")
            GO_BACK_PAGE -> return false
            GO_HOME_PAGE -> return true
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

    const val TAKE_TO_HOME = 1
    const val READ_IN_READING_ROOM = 2
    const val FULL_INFORMATION = 3
    const val RETURN_TO_LIBRARY = 4
    const val BRIEF_INFORMATION = 5
    const val GO_BACK_PAGE = 6
    const val GO_HOME_PAGE = 7
}