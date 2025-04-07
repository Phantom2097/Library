package presentation.show_manager

import ru.phantom.library.data.entites.shop.book.BookShop
import ru.phantom.library.data.entites.shop.disk.DiskShop
import ru.phantom.library.data.entites.shop.newspaper.NewspaperShop
import presentation.colors.Colors.ANSI_RESET
import presentation.colors.Colors.ANSI_YELLOW


fun selectShop(shopNumber: Int): Boolean {
    when (shopNumber) {
        1 -> goToShop(BookShop)
        2 -> goToShop(NewspaperShop)
        3 -> goToShop(DiskShop)
        4 -> return true
        else -> {
            println("${ANSI_YELLOW}Попробуйте ещё раз\n$ANSI_RESET")
            return false
        }
    }
    return true
}