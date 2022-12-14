package vn.ztech.software.projectgutenberg.data.repository

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.source.BookDataSource

class BookRepository private constructor(
    private val remote: BookDataSource.Remote,
    private val local: BookDataSource.Local
) : BookDataSource.Remote, BookDataSource.Local {

    override fun getBooksLocal(listener: OnResultListener<List<Book>>) {
        //TODO Implement later
    }

    override fun getBooks(page: Int, listener: OnResultListener<BaseAPIResponse<Book>>) {
        remote.getBooks(page, listener)
    }

    companion object {
        private var instance: BookRepository? = null

        fun getInstance(remote: BookDataSource.Remote, local: BookDataSource.Local) =
            synchronized(this) {
                instance ?: BookRepository(remote, local).also { instance = it }
            }
    }
}
