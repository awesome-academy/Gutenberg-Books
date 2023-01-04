package vn.ztech.software.projectgutenberg.screen.home

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookRepository
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.extension.toBaseData

class ListBookPresenter internal constructor(
    private val bookRepository: BookRepository
) : ListBookContract.Presenter {
    private var mView: ListBookContract.View? = null
    var isLastPage = false
    override fun getBooks(page: Int, loadingArea: Constant.LoadingArea) {
        // This page variable is for testing
        // delete this variable or replace with a proper implementation later
        mView?.updateLoading(loadingArea, Constant.LoadingState.SHOW)

        bookRepository.remoteReposObj.getBooks(
            page,
            object : OnResultListener<BaseAPIResponse<Book>> {
                override fun onSuccess(data: BaseAPIResponse<Book>) {
                    if (data.next == null || data.next == Constant.STRING_NULL)
                        isLastPage = true
                    mView?.onGetBooksSuccess(data.toBaseData(), loadingArea)
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                }

                override fun onError(e: Exception?) {
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                    mView?.onError(e)
                }
            })
    }

    override fun getBooksWithFilters(
        page: Int,
        filters: Map<BookDataSource.Companion.BookFilter, String>,
        loadingArea: Constant.LoadingArea,
    ) {
        mView?.updateLoading(loadingArea, Constant.LoadingState.SHOW)

        bookRepository.remoteReposObj.getBooksWithFilters(
            page,
            filters,
            object : OnResultListener<BaseAPIResponse<Book>> {
                override fun onSuccess(data: BaseAPIResponse<Book>) {
                    if (data.next == null || data.next == Constant.STRING_NULL)
                        isLastPage = true
                    mView?.onGetBooksSuccess(data.toBaseData(), loadingArea)
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                }

                override fun onError(e: Exception?) {
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                    mView?.onError(e)
                }
            })
    }

    override fun getAllRecentReadingBook(
        loadingArea: Constant.LoadingArea,
        state: Constant.LoadingState
    ) {
        mView?.updateLoading(loadingArea, state)

        bookRepository.localReadableObj.getAllRecentReadingBook(
            object : OnResultListener<List<BookLocal>> {
                override fun onSuccess(data: List<BookLocal>) {
                    mView?.onGetRecentReadingBookSuccess(data, loadingArea)
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                }

                override fun onError(e: Exception?) {
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                    mView?.onError(e)
                }
            })
    }

    override fun setView(view: ListBookContract.View?) {
        mView = view
    }
}
