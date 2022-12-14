package vn.ztech.software.projectgutenberg.data.repository.source.remote

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.ModelCommon
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.remote.api.APIQuery
import vn.ztech.software.projectgutenberg.data.repository.source.remote.utils.NetworkCall
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource

class BookRemoteDataSource : BookDataSource.Remote {

    override fun getBooks(page: Int, listener: OnResultListener<BaseAPIResponse<Book>>) {
        NetworkCall(
            urlString = APIQuery.getBookAPI(page),
            listener,
            ModelCommon.BOOK
        )
    }

    override fun getBooksWithFilters(
        page: Int,
        filters: Map<BookDataSource.Companion.BookFilter, String>,
        listener: OnResultListener<BaseAPIResponse<Book>>,
    ) {
        NetworkCall(
            urlString = APIQuery.getBooksWithFilterAPI(page, filters),
            listener,
            ModelCommon.BOOK
        )
    }

    companion object {
        private var instance: BookRemoteDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: BookRemoteDataSource().also { instance = it }
        }
    }
}
