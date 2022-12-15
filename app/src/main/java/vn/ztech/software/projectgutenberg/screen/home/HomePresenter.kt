package vn.ztech.software.projectgutenberg.screen.home

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.BookRepository
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener

class HomePresenter internal constructor(
    private val bookRepository: BookRepository
) : HomeContract.Presenter {
    private var mView: HomeContract.View? = null

    override fun getBooks() {
        // This page variable is for testing
        // delete this variable or replace with a proper implementation later
        val page = 2
        bookRepository.getBooks(page, object : OnResultListener<BaseAPIResponse<Book>> {
            override fun onSuccess(data: BaseAPIResponse<Book>) {
                mView?.onGetBooksSuccess(data.results)
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
