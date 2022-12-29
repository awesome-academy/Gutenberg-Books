package vn.ztech.software.projectgutenberg.screen.readbook

import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookRepository
import vn.ztech.software.projectgutenberg.utils.Constant

class ReadBookPresenter internal constructor(
    private val bookRepository: BookRepository
) : ReadBookContract.Presenter {
    private var mView: ReadBookContract.View? = null
    override fun getToc(
        bookId: Int,
        loadingArea: Constant.LoadingArea
    ) {
        mView?.updateLoading(loadingArea, Constant.LoadingState.SHOW)
        bookRepository.getToc(bookId, object : OnResultListener<Toc> {
            override fun onSuccess(data: Toc) {
                data.listItem.sortBy { it.idx }
                if (data.listItem.first().title.isEmpty()) data.listItem.first().title =
                    TocItem.STRING_COVER
                mView?.onGetTocSuccess(data, loadingArea)
                mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
            }

            override fun onError(e: Exception?) {
                mView?.onError(e)
                mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
            }
        })
    }


    override fun onStart() {
        // TODO implement later
    }

    override fun onStop() {
        // TODO implement later
    }

    override fun setView(view: ReadBookContract.View?) {
        mView = view
    }
}
