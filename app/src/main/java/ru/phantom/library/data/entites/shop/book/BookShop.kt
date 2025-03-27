package ru.phantom.library.data.entites.shop.book

import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.book.BookImpl
import data.entites.shop.Shop
import ru.phantom.library.data.entites.shop.book.BookShop.BookShopConsts.BOOK_SHOP_ID
import ru.phantom.library.data.entites.shop.book.BookShop.BookShopConsts.START_COUNTER_INDEX
import ru.phantom.library.domain.LibraryService

private typealias BookShopType = Shop<BookImpl>

object BookShop : BookShopType {

    private var counter = START_COUNTER_INDEX

    override fun sell(): BookImpl {
        return BookImpl(
            LibraryItem(
                name = "Книга из магазина $counter",
                id = BOOK_SHOP_ID + counter++,
                availability = true,
            ),
            LibraryService()
        ).apply {
            this.author = "МАГАЗИН"
            this.numberOfPages = Int.MAX_VALUE
        }
    }

    private object BookShopConsts {
        const val START_COUNTER_INDEX = 0
        const val BOOK_SHOP_ID = 1000
    }
}