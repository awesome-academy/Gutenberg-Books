package vn.ztech.software.projectgutenberg.screen.home

import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.utils.base.BasePresenter
import vn.ztech.software.projectgutenberg.utils.base.BaseView

interface ListBookshelfContract {

    interface View : BaseView {
        fun onGetBookshelvesSuccess(bookshelves: List<Bookshelf>)
        fun onError(e: Exception?)
    }

    interface Presenter : BasePresenter<View> {
        fun getBookshelves()
    }
}
