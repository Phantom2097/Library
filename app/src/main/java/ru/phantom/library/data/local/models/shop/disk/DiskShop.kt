package ru.phantom.library.data.local.models.shop.disk

import ru.phantom.library.data.local.models.library.items.LibraryItem
import ru.phantom.library.data.local.models.library.items.disk.DiskImpl
import ru.phantom.library.domain.entities.library.disk.Type
import ru.phantom.library.domain.entities.shop.Shop
import ru.phantom.library.data.local.models.shop.disk.DiskShop.DiskShopConsts.DISK_SHOP_ID
import ru.phantom.library.data.local.models.shop.disk.DiskShop.DiskShopConsts.START_TYPE_INDEX
import ru.phantom.library.domain.library_service.LibraryService

private typealias DiskShopType = Shop<DiskImpl>

object DiskShop : DiskShopType {

    private var counter = DiskShopConsts.START_COUNTER_INDEX

    override fun sell(): DiskImpl {
        return DiskImpl(
            LibraryItem(
                name = "Диск из магазина $counter",
                id = DISK_SHOP_ID + counter++,
                availability = true,
            ),
            LibraryService
        ).apply {
            val typeSize = Type.entries.size
            type = Type.entries[(START_TYPE_INDEX..< typeSize).random()]
        }
    }

    private object DiskShopConsts {
        const val START_TYPE_INDEX = 0
        const val START_COUNTER_INDEX = 0
        const val DISK_SHOP_ID = 2000L
    }
}