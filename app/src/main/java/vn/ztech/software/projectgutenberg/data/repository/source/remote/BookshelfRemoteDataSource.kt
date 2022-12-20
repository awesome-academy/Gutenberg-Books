package vn.ztech.software.projectgutenberg.data.repository.source.remote

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.data.model.ModelCommon
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.remote.api.APIQuery
import vn.ztech.software.projectgutenberg.data.repository.source.remote.utils.NetworkCall
import vn.ztech.software.projectgutenberg.data.repository.source.repository.bookshelf.BookshelfDataSource

class BookshelfRemoteDataSource : BookshelfDataSource.Remote {

    override fun getBookshelves(page: Int, listener: OnResultListener<BaseAPIResponse<Bookshelf>>) {
        NetworkCall(
            urlString = APIQuery.getBookselfAPI(page),
            listener,
            ModelCommon.BOOKSHELF
        )
    }

    companion object {
        private var instance: BookshelfRemoteDataSource? = null

        fun getInstance() = synchronized(this) {
            instance ?: BookshelfRemoteDataSource().also { instance = it }
        }
    }
}
