package ru.phantom.common.models.shop.newspaper

import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.models.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.common.entities.library.newspaper.Newspaper
import ru.phantom.common.entities.library.newspaper.month.Month
import ru.phantom.common.entities.shop.Shop
import ru.phantom.common.library_service.LibraryService

private typealias NewspaperShopType = Shop<Newspaper>

object NewspaperShop : NewspaperShopType {

    private var counter = NewspaperShopConsts.START_COUNTER_INDEX

    override fun sell(): Newspaper {
        return NewspaperWithMonthImpl(
            LibraryItem(
                name = "Газета из магазина $counter",
                id = NewspaperShopConsts.NEWSPAPER_SHOP_ID + counter++,
                availability = true,
            ),
            LibraryService
        ).apply {
            issueNumber = (NewspaperShopConsts.START_ISSUE_NUMBER..<NewspaperShopConsts.END_ISSUE_NUMBER).random()
            issueMonth = Month.entries.random()
        }
    }

    private object NewspaperShopConsts {
        const val START_COUNTER_INDEX = 0
        const val NEWSPAPER_SHOP_ID = 3000L
        const val START_ISSUE_NUMBER = 10000
        const val END_ISSUE_NUMBER = 100000
    }
}