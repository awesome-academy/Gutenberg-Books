package vn.ztech.software.projectgutenberg.data.repository.source.repository.bookshelf

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener

interface BookshelfDataSource {

    interface Remote {
        fun getBookshelves(page: Int, listener: OnResultListener<BaseAPIResponse<Bookshelf>>)
    }
}
