package vn.ztech.software.projectgutenberg.screen.home

import vn.ztech.software.projectgutenberg.data.model.BaseData
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BasePresenter
import vn.ztech.software.projectgutenberg.utils.base.BaseView

interface ListBookContract {

    interface View : BaseView {
        fun onGetBooksSuccess(data: BaseData<Book>, loadingArea: Constant.LoadingArea)
        fun onGetRecentReadingBookSuccess(data: List<BookLocal>, loadingArea: Constant.LoadingArea)
        fun onError(e: Exception?)
    }

    interface Presenter : BasePresenter<View> {
        fun getBooks(
            page: Int = 1,
            loadingArea: Constant.LoadingArea = Constant.LoadingAreaHome.HomeListBook
        )

        fun getBooksWithFilters(
            page: Int = 1,
            filters: Map<BookDataSource.Companion.BookFilter, String>,
            loadingArea: Constant.LoadingArea = Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthor
        )

        fun getAllRecentReadingBook(
            loadingArea: Constant.LoadingArea = Constant.LoadingAreaHome.HomeRecentReading,
            state: Constant.LoadingState = Constant.LoadingState.SHOW
        )
    }
}
