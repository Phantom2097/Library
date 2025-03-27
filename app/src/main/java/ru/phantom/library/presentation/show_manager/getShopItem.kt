package presentation.show_manager

import ru.phantom.library.data.entites.manager.Manager
import data.entites.shop.Shop
import data.repository.LibraryRepository.addItem

fun <T> getShopItem(shop: Shop<T>, count: Int) {
    repeat(count) {
        val manager = Manager<T> { it.sell() }
        val item = manager.buy(shop)

        addItem(item)
    }
}