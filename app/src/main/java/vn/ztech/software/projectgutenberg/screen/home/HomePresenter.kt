package vn.ztech.software.projectgutenberg.screen.home

import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.BookRepository
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import java.lang.Exception

class HomePresenter internal constructor(
    private val bookRepository: BookRepository
): HomeContract.Presenter {
    private var mView: HomeContract.View? = null

    override fun getBooks() {
        bookRepository.getBooks(object : OnResultListener<List<Book>>{
            override fun onSuccess(data: List<Book>) {
                mView?.onGetBooksSuccess(data)
            }

            override fun onError(e: Exception?) {
                mView?.onError(e)
            }
        })
    }

    override fun onStart() {
        //TODO Implement later
    }

    override fun onStop() {
        //TODO Implement later
    }

    override fun setView(view: HomeContract.View?) {
        mView = view
    }

}
