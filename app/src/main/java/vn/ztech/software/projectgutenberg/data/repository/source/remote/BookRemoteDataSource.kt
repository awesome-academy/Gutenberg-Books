package vn.ztech.software.projectgutenberg.data.repository.source.remote

import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.BookDataSource

class BookRemoteDataSource: BookDataSource.Remote {

    override fun getBooks(listener: OnResultListener<List<Book>>) {
        listener.onSuccess(listOf(Book("War and peace")))
    }

    companion object {
        private var instance: BookRemoteDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: BookRemoteDataSource().also { instance = it }
        }
    }
}
