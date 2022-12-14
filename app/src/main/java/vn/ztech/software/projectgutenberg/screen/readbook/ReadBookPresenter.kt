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
        bookRepository.localReadableObj.getToc(bookId, object : OnResultListener<Toc> {
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

    override fun updateReadingProgress(tocItem: TocItem, readingProgressString: String) {
        bookRepository.localWritableObj.updateReadingProgress(tocItem.bookId, tocItem.href, readingProgressString,
            object : OnResultListener<Boolean> {
                override fun onSuccess(isSuccess: Boolean) {
                    tocItem.progress = readingProgressString
                    if (isSuccess) mView?.onUpdateReadingProgressDone(tocItem)
                }

                override fun onError(e: Exception?) {
                    //todo leave this fun blank, this error can be ignore silently, it is not too critical
                }
            })
    }

    override fun updateLatestReadingTocItem(tocItem: TocItem) {
        bookRepository.localWritableObj.updateLatestReadingTocItem(tocItem)
    }

    override fun setView(view: ReadBookContract.View?) {
        mView = view
    }
}
