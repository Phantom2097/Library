package ru.phantom.common.models.shop.disk

import ru.phantom.common.models.library.items.LibraryItem
import ru.phantom.common.models.library.items.disk.DiskImpl
import ru.phantom.common.models.shop.disk.DiskShop.DiskShopConsts.DISK_SHOP_ID
import ru.phantom.common.models.shop.disk.DiskShop.DiskShopConsts.START_TYPE_INDEX
import ru.phantom.common.entities.library.disk.Type
import ru.phantom.common.entities.shop.Shop
import ru.phantom.common.library_service.LibraryService

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