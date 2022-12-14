package vn.ztech.software.projectgutenberg.screen.download

import android.content.Context
import vn.ztech.software.projectgutenberg.data.model.BaseDataLocal
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.screen.download.DownloadFragment.Companion.GetBooksActionType
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BasePresenter
import vn.ztech.software.projectgutenberg.utils.base.BaseView

interface ListBookLocalContract {

    interface View : BaseView {

        fun onGetBooksSuccess(
            data: BaseDataLocal<BookLocal>,
            loadingArea: Constant.LoadingArea,
            action: GetBooksActionType = GetBooksActionType.LOAD
        )

        fun onScanBookComplete(data: Boolean, loadingArea: Constant.LoadingArea)
        fun onError(e: Exception?)
        fun onHitLastPage(page: Int)
        fun onDeleteBookComplete(data: Boolean)
        fun setLoadMore(enable: Boolean)
        fun onUnzipBookSuccess(book: BookLocal)
        fun onUnzipBookFailed(book: BookLocal)
        fun onParseEpubDone(book: BookLocal, success: BasePresenter.Companion.Result)
    }

    interface Presenter : BasePresenter<View> {
        fun getDownloadedBooks(
            offset: Int = 0,
            loadingArea: Constant.LoadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain,
            action: GetBooksActionType = GetBooksActionType.LOAD
        )

        fun scanLocalStorage(context: Context, loadingArea: Constant.LoadingArea)

        fun deleteBookLocal(
            context: Context,
            book: BookLocal,
            loadingArea: Constant.LoadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain
        )

        fun searchBooksLocal(
            keyword: String,
            loadingArea: Constant.LoadingArea = Constant.LoadingAreaDownloadedBook.DownloadedBookMain,
            action: GetBooksActionType = GetBooksActionType.SILENT_REFRESH
        )

        fun refresh()
        fun unzipBook(context: Context?, book: BookLocal)
        fun parseEpubFile(book: BookLocal, providerUnzippedBookDirectoryPath: String)
        fun updateNewDataAndLoadMore(offset: Int)
    }
}
