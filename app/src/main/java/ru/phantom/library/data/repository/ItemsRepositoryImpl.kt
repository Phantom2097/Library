package ru.phantom.library.data.repository

import android.accounts.NetworkErrorException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.phantom.library.data.Position
import ru.phantom.library.data.local.models.library.items.BasicLibraryElement
import ru.phantom.library.data.local.models.library.items.LibraryItem
import ru.phantom.library.data.local.models.library.items.disk.DiskImpl
import ru.phantom.library.domain.entities.library.disk.Type.CD
import ru.phantom.library.domain.entities.library.disk.Type.DVD
import ru.phantom.library.data.local.models.library.items.newspaper.NewspaperImpl
import ru.phantom.library.domain.entities.library.newspaper.month.Month.JANUARY
import ru.phantom.library.data.local.models.library.items.newspaper.newspaper_with_month.NewspaperWithMonthImpl
import ru.phantom.library.data.repository.LibraryRepository.getItemsCounter
import ru.phantom.library.domain.library_service.LibraryElementFactory.createBook
import ru.phantom.library.domain.library_service.LibraryElementFactory.createDisk
import ru.phantom.library.domain.library_service.LibraryElementFactory.createNewspaper
import ru.phantom.library.domain.library_service.LibraryService
import kotlin.random.Random

class ItemsRepositoryImpl : ItemsRepository<BasicLibraryElement> {

    private val _allItems = mutableListOf<BasicLibraryElement>()
    private var isNeedLoad = true

    private var errorCounter = ERROR_COUNTER_INIT

    init {
        _allItems.addAll(createBooks())
        _allItems.addAll(createNewspapers())
        _allItems.addAll(createDisks())
    }

    override suspend fun addItems(
        item: BasicLibraryElement
    ) {
        withContext(Dispatchers.IO) {
            _allItems.add(item)
        }
    }

    override suspend fun removeItem(position: Int) {
        withContext(Dispatchers.IO) {
            if (position in _allItems.indices) {
                _allItems.removeAt(position)
            }
        }
    }

    override suspend fun getItems(): List<BasicLibraryElement> {
        if (isNeedLoad) {
            delay(START_DELAY)
            isNeedLoad = false
        }
        return _allItems.toList()
    }

    override suspend fun changeItem(
        position: Int,
        newItem: BasicLibraryElement
    ) {
        withContext(Dispatchers.IO) {
            _allItems[position] = newItem
        }
    }

    /**
     * Эмулирует задержку в диапазоне от 100мс до 2000мс
     */
    suspend fun delayEmulator() {
        val time = Random.nextLong(RANDOM_START, RANDOM_END)
        delay(time)
    }

    /**
     * Эмулирует состояние ошибки каждый 5ый раз
     */
    fun errorEmulator() {
        val isError = ++errorCounter % ERROR_FREQUENCY == ERROR_COUNTER_COMPARE
        if (isError) {
            errorCounter = ERROR_COUNTER_INIT
            throw NetworkErrorException("Ошибка загрузки данных, попробуйте ещё раз")
        }
    }

    /**
     * Создание книг
     */
    private fun createBooks() = buildList {
        add(
            createBook(
                name = "Котлин для профессионалов",
                author = "Джош Скин, Дэвид Грэнхол, Эндрю Бэйли",
                numberOfPages = 560,
                service = service
            )
        )

        add(
            createBook(
                name = "Маугли",
                availability = false,
                author = "Джозеф Киплинг",
                numberOfPages = 202,
                service = service
            )
        )
        add(
            createBook(
                name = "Kotlin Design Patterns and Best Practices",
                availability = false,
                position = Position.IN_READING_ROOM,
                author = "Alexey Soshin, Anton Arhipov",
                numberOfPages = 356,
                service = service
            )
        )
        add(
            createBook(
                name = "Евгений Онегин",
                availability = false,
                position = Position.HOME,
                author = "Пушкин А.С.",
                numberOfPages = 320,
                service = service
            )
        )
        add(
            createBook(
                name = "Алые Плинтуса",
                id = getItemsCounter(),
                availability = true,
                author = "Саша Зелёный",
                service = service
            )
        )
        add(
            createBook(
                name = "Война и привет",
                id = getItemsCounter(),
                availability = true,
                service = service
            )
        )
    }

    /**
     * Создание газет
     */
    private fun createNewspapers() = buildList {
        add(
            createNewspaper(
                name = "Русская правда",
                id = getItemsCounter(),
                availability = false,
                position = Position.IN_READING_ROOM,
                service = service,
                issueNumber = 794
            )
        )
        add(
            NewspaperImpl(
                LibraryItem(
                    name = "Русская правда",
                    id = getItemsCounter(),
                    availability = true,
                ),
                service

            ).apply {
                issueNumber = 795
            })
        add(
            NewspaperImpl(
                LibraryItem(
                    name = "Русская правда",
                    id = getItemsCounter(),
                    availability = true,
                ),
                service

            ).apply {
                issueNumber = 796
            })
        add(
            NewspaperImpl(
                LibraryItem(
                    name = "Русская ложь",
                    id = getItemsCounter(),
                    availability = true,
                ),
                service
            )
        )
        add(
            NewspaperWithMonthImpl(
                LibraryItem(
                    name = "Русская правда",
                    id = getItemsCounter(),
                    availability = true,
                ),
                service
            ).apply {
                issueNumber = 795
                issueMonth = JANUARY
            })
    }

    /**
     * Создание дисков
     */
    private fun createDisks() = buildList {
        add(
            DiskImpl(
                LibraryItem(
                    name = "Дэдпул и Росомаха",
                    id = getItemsCounter(),
                    availability = true,
                ),
                service

            ).apply {
                type = DVD
            })

        add(
            DiskImpl(
                LibraryItem(
                    name = "Какая-то песня",
                    id = getItemsCounter(),
                    availability = true,
                ),
                service

            ).apply {
                type = CD
            })

        add(
            createDisk(
                name = "Просто диск",
                id = getItemsCounter(),
                availability = false,
                position = Position.HOME,
                service = service
            )
        )

        add(
            DiskImpl(
                LibraryItem(
                    name = "1111",
                    id = getItemsCounter(),
                    availability = false,
                    position = Position.HOME
                ),
                service
            )
        )
    }

    companion object {
        private val service = LibraryService

        private const val ERROR_COUNTER_INIT = 0
        private const val ERROR_COUNTER_COMPARE = 0
        private const val ERROR_FREQUENCY = 5

        private const val RANDOM_START = 1000L
        private const val RANDOM_END = 2000L

        private const val START_DELAY = 2500L
    }
}