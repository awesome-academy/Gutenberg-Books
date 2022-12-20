package vn.ztech.software.projectgutenberg.screen.home

import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BasePresenter
import vn.ztech.software.projectgutenberg.utils.base.BaseView

interface ListBookContract {

    interface View : BaseView {
        fun onGetBooksSuccess(books: List<Book>)
        fun onError(e: Exception?)
    }

    interface Presenter : BasePresenter<View> {
        fun getBooks(
            page: Int = Constant.FIRST_PAGE,
            loadingArea: Constant.LoadingArea = Constant.LoadingArea.HomeListBook
        )
    }
}
