package ru.phantom.library.data.entites.shop.newspaper

import ru.phantom.library.data.entites.library.items.LibraryItem
import ru.phantom.library.data.entites.library.items.newspaper.Newspaper
import ru.phantom.library.data.entites.library.items.newspaper.newspaper_with_month.Month
import ru.phantom.library.data.entites.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.library.data.entites.shop.Shop
import ru.phantom.library.data.entites.shop.newspaper.NewspaperShop.NewspaperShopConsts.END_ISSUE_NUMBER
import ru.phantom.library.data.entites.shop.newspaper.NewspaperShop.NewspaperShopConsts.NEWSPAPER_SHOP_ID
import ru.phantom.library.data.entites.shop.newspaper.NewspaperShop.NewspaperShopConsts.START_COUNTER_INDEX
import ru.phantom.library.data.entites.shop.newspaper.NewspaperShop.NewspaperShopConsts.START_ISSUE_NUMBER
import ru.phantom.library.domain.library_service.LibraryService

private typealias NewspaperShopType = Shop<Newspaper>

object NewspaperShop : NewspaperShopType {

    private var counter = START_COUNTER_INDEX

    override fun sell(): Newspaper {
        return NewspaperWithMonthImpl(
            LibraryItem(
                name = "Газета из магазина $counter",
                id = NEWSPAPER_SHOP_ID + counter++,
                availability = true,
            ),
            LibraryService
        ).apply {
            issueNumber = (START_ISSUE_NUMBER ..< END_ISSUE_NUMBER).random()
            issueMonth = Month.entries.random()
        }
    }

    private object NewspaperShopConsts {
        const val START_COUNTER_INDEX = 0
        const val NEWSPAPER_SHOP_ID = 3000
        const val START_ISSUE_NUMBER = 10000
        const val END_ISSUE_NUMBER = 100000
    }
}