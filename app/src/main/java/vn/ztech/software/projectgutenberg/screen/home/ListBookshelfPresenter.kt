package vn.ztech.software.projectgutenberg.screen.home

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.repository.bookshelf.BookshelfRepository
import vn.ztech.software.projectgutenberg.utils.Constant

class ListBookshelfPresenter internal constructor(
    private val bookshelfRepository: BookshelfRepository
) : ListBookshelfContract.Presenter {
    private var mView: ListBookshelfContract.View? = null

    override fun getBookshelves() {
        // This page variable is for testing
        // delete this variable or replace with a proper implementation later
        mView?.updateLoading(Constant.LoadingAreaHome.HomeBookshelf, Constant.LoadingState.SHOW)
        /** Get random page */
        val page = (Math.random() * Constant.PAGE_NUMBER_RANDOM_RANGE).toInt() + Constant.FIRST_PAGE
        bookshelfRepository.getBookshelves(
            page,
            object : OnResultListener<BaseAPIResponse<Bookshelf>> {
                override fun onSuccess(data: BaseAPIResponse<Bookshelf>) {
                    mView?.onGetBookshelvesSuccess(data.results)
                    mView?.updateLoading(
                        Constant.LoadingAreaHome.HomeBookshelf,
                        Constant.LoadingState.HIDE
                    )
                }

                override fun onError(e: Exception?) {
                    mView?.updateLoading(
                        Constant.LoadingAreaHome.HomeBookshelf,
                        Constant.LoadingState.HIDE
                    )
                    mView?.onError(e)
                }
            })
    }

    override fun setView(view: ListBookshelfContract.View?) {
        mView = view
    }
}
