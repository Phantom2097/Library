package presentation.show_manager

import ru.phantom.common.library_service.LibraryRepository.addItem
import ru.phantom.common.entities.manager.Manager
import ru.phantom.common.entities.shop.Shop

fun <T> getShopItem(shop: Shop<T>, count: Int) {
    repeat(count) {
        val manager = Manager<T> { it.sell() }
        val item = manager.buy(shop)

        addItem(item)
    }
}