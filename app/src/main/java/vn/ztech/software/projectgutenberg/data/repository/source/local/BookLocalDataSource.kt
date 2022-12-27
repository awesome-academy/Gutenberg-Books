package vn.ztech.software.projectgutenberg.data.repository.source.local

import android.content.Context
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.deleteBook
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.fetchBooks
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookDbDAO
import vn.ztech.software.projectgutenberg.data.repository.source.local.utils.AsynchronousCall
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource


class BookLocalDataSource(private val bookDbDAO: BookDbDAO) : BookDataSource.Local {

    override fun getBooksLocal(offset: Int, listener: OnResultListener<List<BookLocal>>) {
        AsynchronousCall<List<BookLocal>>(
            {
                val result = bookDbDAO.readableDAOObj.getBooks(offset)
                result
            },
            listener
        )
    }

    override fun scanLocalStorage(context: Context, onResultListener: OnResultListener<Boolean>) {
        AsynchronousCall(
            {
                val booksInStorage = fetchBooks(context)
                bookDbDAO.writableDAOObj.clearNotExistItems(booksInStorage)
                val result = bookDbDAO.writableDAOObj.insertBooks(booksInStorage)
                result
            },
            onResultListener
        )
    }

    override fun updateBookImageUrl(res: Resource, fileTitle: String, imgUrl: String) {
        AsynchronousCall<Boolean>(
            {
                val result = bookDbDAO.writableDAOObj.updateImageUrl(res, fileTitle, imgUrl)
                result
            },
            null
        )
    }

    override fun deleteBookLocal(
        context: Context,
        bookLocal: BookLocal,
        listener: OnResultListener<Boolean>
    ) {
        AsynchronousCall<Boolean>(
            {
                var resultDeleteFromStorage = false
                var resultDeleteFromSQLite = false

                resultDeleteFromStorage = deleteBook(context, bookLocal)
                if (resultDeleteFromStorage)
                    resultDeleteFromSQLite = bookDbDAO.writableDAOObj.deleteBook(bookLocal.title)

                (!resultDeleteFromStorage || resultDeleteFromSQLite)
            },
            listener
        )
    }

    override fun searchBookLocal(keyword: String, listener: OnResultListener<List<BookLocal>>) {
        AsynchronousCall<List<BookLocal>>(
            {
                bookDbDAO.readableDAOObj.searchBookLocal(keyword)
            },
            listener
        )
    }

    companion object {
        private var instance: BookLocalDataSource? = null

        fun getInstance(bookDbDAO: BookDbDAO) = synchronized(this) {
            instance ?: BookLocalDataSource(bookDbDAO).also { instance = it }
        }
    }

}
