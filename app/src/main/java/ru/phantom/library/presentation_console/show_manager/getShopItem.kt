package presentation.show_manager

import ru.phantom.library.domain.entities.manager.Manager
import ru.phantom.library.domain.entities.shop.Shop
import ru.phantom.library.data.repository.LibraryRepository.addItem

fun <T> getShopItem(shop: Shop<T>, count: Int) {
    repeat(count) {
        val manager = Manager<T> { it.sell() }
        val item = manager.buy(shop)

        addItem(item)
    }
}