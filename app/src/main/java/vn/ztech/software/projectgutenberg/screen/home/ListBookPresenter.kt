package vn.ztech.software.projectgutenberg.screen.home

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookRepository
import vn.ztech.software.projectgutenberg.utils.Constant

class ListBookPresenter internal constructor(
    private val bookRepository: BookRepository
) : ListBookContract.Presenter {
    private var mView: ListBookContract.View? = null
    var isLastPage = false
    override fun getBooks(page: Int, loadingArea: Constant.LoadingArea) {
        // This page variable is for testing
        // delete this variable or replace with a proper implementation later
        mView?.updateLoading(loadingArea, Constant.LoadingState.SHOW)

        bookRepository.getBooks(page, object : OnResultListener<BaseAPIResponse<Book>> {
            override fun onSuccess(data: BaseAPIResponse<Book>) {
                if (data.next == null || data.next == Constant.STRING_NULL)
                    isLastPage = true
                mView?.onGetBooksSuccess(data.results)
                mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
            }

            override fun onError(e: Exception?) {
                mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
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

    override fun setView(view: ListBookContract.View?) {
        mView = view
    }
}