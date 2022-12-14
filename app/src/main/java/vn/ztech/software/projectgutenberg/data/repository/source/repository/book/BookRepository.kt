package vn.ztech.software.projectgutenberg.data.repository.source.repository.book

import android.content.Context
import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener

class BookRepository private constructor(
    private val remote: BookDataSource.Remote,
    private val local: BookDataSource.Local
) : BookDataSource.Remote, BookDataSource.Local {

    override fun getBooksLocal(offset: Int, listener: OnResultListener<List<BookLocal>>) {
        local.getBooksLocal(offset, listener)
    }

    override fun scanLocalStorage(
        context: Context,
        onResultListener: OnResultListener<Boolean>
    ) {
        local.scanLocalStorage(context, onResultListener)
    }

    override fun updateBookImageUrl(res: Resource, fileTitle: String, imgUrl: String) {
        local.updateBookImageUrl(res, fileTitle, imgUrl)
    }

    override fun deleteBookLocal(
        context: Context,
        bookLocal: BookLocal,
        listener: OnResultListener<Boolean>
    ) {
        local.deleteBookLocal(context, bookLocal, listener)
    }

    override fun searchBookLocal(keyword: String, listener: OnResultListener<List<BookLocal>>) {
        local.searchBookLocal(keyword, listener)
    }

    override fun getBooks(page: Int, listener: OnResultListener<BaseAPIResponse<Book>>) {
        remote.getBooks(page, listener)
    }

    override fun getBooksWithFilters(
        page: Int,
        filters: Map<BookDataSource.Companion.BookFilter, String>,
        listener: OnResultListener<BaseAPIResponse<Book>>,
    ) {
        remote.getBooksWithFilters(page, filters, listener)
    }

    companion object {
        private var instance: BookRepository? = null

        fun getInstance(remote: BookDataSource.Remote, local: BookDataSource.Local) =
            synchronized(this) {
                instance ?: BookRepository(remote, local).also { instance = it }
            }
    }
}
