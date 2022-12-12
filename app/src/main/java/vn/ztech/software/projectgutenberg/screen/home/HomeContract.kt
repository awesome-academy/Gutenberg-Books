package vn.ztech.software.projectgutenberg.screen.home

import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.utils.base.BasePresenter
import java.lang.Exception

class HomeContract {

    interface View {
        fun onGetBooksSuccess(books: List<Book>)
        fun onError(e: Exception?)
    }

    interface Presenter: BasePresenter<View>{
        fun getBooks()
    }
}
