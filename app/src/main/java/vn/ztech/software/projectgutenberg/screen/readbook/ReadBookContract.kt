package vn.ztech.software.projectgutenberg.screen.readbook

import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.screen.download.DownloadFragment.Companion.GetBooksActionType
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BasePresenter
import vn.ztech.software.projectgutenberg.utils.base.BaseView

interface ReadBookContract {

    interface View : BaseView {
        fun onGetTocSuccess(
            data: Toc,
            loadingArea: Constant.LoadingArea,
            action: GetBooksActionType = GetBooksActionType.LOAD
        )

        fun onUpdateReadingProgressDone(tocItem: TocItem)
        fun onError(e: Exception?)
    }

    interface Presenter : BasePresenter<View> {
        fun getToc(
            bookId: Int,
            loadingArea: Constant.LoadingArea = Constant.LoadingAreaReadBook.ReadBookMain,
        )

        fun updateReadingProgress(tocItem: TocItem, readingProgressString: String)
        fun updateLatestReadingTocItem(tocItem: TocItem)
    }
}
