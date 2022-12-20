package vn.ztech.software.projectgutenberg.data.repository.source.repository.bookshelf

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener

class BookshelfRepository private constructor(
    private val remote: BookshelfDataSource.Remote,
) : BookshelfDataSource.Remote {

    override fun getBookshelves(page: Int, listener: OnResultListener<BaseAPIResponse<Bookshelf>>) {
        remote.getBookshelves(page, listener)
    }

    companion object {
        private var instance: BookshelfRepository? = null

        fun getInstance(remote: BookshelfDataSource.Remote) =
            synchronized(this) {
                instance ?: BookshelfRepository(remote).also { instance = it }
            }
    }
}
