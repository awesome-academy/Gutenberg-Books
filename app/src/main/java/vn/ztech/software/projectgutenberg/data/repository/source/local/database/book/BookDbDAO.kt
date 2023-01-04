package vn.ztech.software.projectgutenberg.data.repository.source.local.database.book

import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookDbDAOImpl.Companion.InsertDuplicateAction

interface BookDbDAOReadableInterface {

    fun getBooks(page: Int): List<BookLocal>

    fun searchBookLocal(keyWord: String): List<BookLocal>
}

interface BookDbDAOWritableInterface {

    fun insertBook(
        book: BookLocal,
        duplicateAction: InsertDuplicateAction = InsertDuplicateAction.KEEP_ORIGINAL
    ): Boolean

    fun insertBooks(
        books: List<BookLocal>,
        duplicateAction: InsertDuplicateAction = InsertDuplicateAction.KEEP_ORIGINAL
    ): Boolean

    fun clearNotExistItems(books: List<BookLocal>): Boolean

    fun deleteBook(title: String): Boolean

    fun updateBook(book: BookLocal, newBook: BookLocal): Int

    fun updateBook(selection: String, selectionArgs: Array<String>, newBook: BookLocal): Int

    fun updateImageUrl(res: Resource, fileTitle: String, imgUrl: String): Boolean

    fun updateImageUrl(selection: String, selectionArgs: Array<String>, imgUrl: String): Boolean

    fun markBookAsPrepared(book: BookLocal): Boolean
}
