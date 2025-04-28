package ru.phantom.library.data.repository.book

//class BookRepository(context: Context) : ItemsRepository<Book> {
//    private val db = (context.applicationContext as App).database
//
//    private val booksFlow = MutableStateFlow<List<Book>>(emptyList())
//    override suspend fun addItems(item: Book) {
//        val (newItem, newBook) = item.toEntity()
//        db.itemDao().insertItem(newItem)
//        db.bookDao().insertBook(newBook)
//    }
//
//    override suspend fun removeItem(position: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getItems(): Flow<List<Book>> {
//        return flow { db.itemDao().getItems().map { it.toBook(db.bookDao().getBookInfoById(it.id)) } }
//    }
//
//    override suspend fun changeItem(
//        position: Int,
//        newItem: Book
//    ) {
//        TODO("Not yet implemented")
//    }
//
//
//}