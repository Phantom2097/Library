package ru.phantom.common.models.shop.book

import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.models.library.items.book.BookImpl
import ru.phantom.common.entities.shop.Shop
import ru.phantom.common.library_service.LibraryService

private typealias BookShopType = Shop<BookImpl>

object BookShop : BookShopType {

    private var counter = BookShopConsts.START_COUNTER_INDEX

    override fun sell(): BookImpl {
        return BookImpl(
            LibraryItem(
                name = "Книга из магазина $counter",
                id = BookShopConsts.BOOK_SHOP_ID + counter++,
                availability = true,
            ),
            LibraryService
        ).apply {
            this.author = listOf("МАГАЗИН")
            this.numberOfPages = Int.MAX_VALUE
        }
    }

    private object BookShopConsts {
        const val START_COUNTER_INDEX = 0
        const val BOOK_SHOP_ID = 1000L
    }
}