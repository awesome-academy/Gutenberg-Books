package vn.ztech.software.projectgutenberg.screen.download

import android.content.Context
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookRepository
import vn.ztech.software.projectgutenberg.screen.download.DownloadFragment.Companion.GetBooksActionType
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.Constant.EMPTY_STRING
import vn.ztech.software.projectgutenberg.utils.base.BasePresenter.Companion.Result
import vn.ztech.software.projectgutenberg.utils.extension.toBaseDataLocal

class ListBookLocalPresenter internal constructor(
    private val bookRepository: BookRepository
) : ListBookLocalContract.Presenter {

    private var mView: ListBookLocalContract.View? = null
    private var currentAction = DownloadFragment.Companion.ActionType.LOAD
    private var currentKeyword = EMPTY_STRING

    override fun getDownloadedBooks(
        offset: Int,
        loadingArea: Constant.LoadingArea,
        action: GetBooksActionType
    ) {
        currentAction = DownloadFragment.Companion.ActionType.LOAD
        val loading =
            if (action == GetBooksActionType.SILENT_REFRESH) Constant.LoadingState.HIDE
            else Constant.LoadingState.SHOW
        mView?.updateLoading(loadingArea, loading)

        bookRepository.localReadableObj.getBooksLocal(offset, object : OnResultListener<List<BookLocal>> {
            override fun onSuccess(data: List<BookLocal>) {
                if (data.isEmpty() || data.size < Constant.LOCAL_DATA_QUERY_PAGE_SIZE) mView?.onHitLastPage(
                    offset
                )
                mView?.onGetBooksSuccess(
                    data.toBaseDataLocal(enableNextPage = true),
                    loadingArea,
                    action
                )

                mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
            }

            override fun onError(e: Exception?) {
                mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                mView?.onError(e)
            }
        })
    }

    override fun scanLocalStorage(context: Context, loadingArea: Constant.LoadingArea) {
        mView?.updateLoading(loadingArea, Constant.LoadingState.SHOW)

        bookRepository.localReadableObj.scanLocalStorage(
            context,
            object : OnResultListener<Boolean> {
                override fun onSuccess(data: Boolean) {
                    mView?.onScanBookComplete(data, loadingArea)
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                }

                override fun onError(e: Exception?) {
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                    mView?.onError(e)
                }
            }
        )
    }

    override fun deleteBookLocal(
        context: Context,
        book: BookLocal,
        loadingArea: Constant.LoadingArea
    ) {
        mView?.updateLoading(loadingArea, Constant.LoadingState.SHOW)

        bookRepository.localWritableObj.deleteBookLocal(
            context = context,
            book,
            object : OnResultListener<Boolean> {
                override fun onSuccess(data: Boolean) {
                    mView?.onDeleteBookComplete(data)
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                }

                override fun onError(e: Exception?) {
                    mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                    mView?.onError(e)
                }

            }
        )
    }

    override fun searchBooksLocal(
        keyword: String,
        loadingArea: Constant.LoadingArea,
        action: GetBooksActionType
    ) {
        if (keyword.isNullOrEmpty()) {
            currentAction = DownloadFragment.Companion.ActionType.LOAD
            getDownloadedBooks(action = GetBooksActionType.REFRESH)
            mView?.setLoadMore(true)
            return
        }
        /**Disable loadMore in search feature*/
        currentAction = DownloadFragment.Companion.ActionType.SEARCH
        currentKeyword = keyword
        mView?.setLoadMore(false)
        val loading =
            if (action == GetBooksActionType.SILENT_REFRESH) Constant.LoadingState.HIDE
            else Constant.LoadingState.SHOW
        mView?.updateLoading(loadingArea, loading)

        bookRepository.localReadableObj.searchBookLocal(keyword, object : OnResultListener<List<BookLocal>> {
            override fun onSuccess(data: List<BookLocal>) {
                mView?.onGetBooksSuccess(
                    data.toBaseDataLocal(enableNextPage = true),
                    loadingArea,
                    action
                )
                mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
            }

            override fun onError(e: Exception?) {
                mView?.updateLoading(loadingArea, Constant.LoadingState.HIDE)
                mView?.onError(e)
            }
        })
    }

    override fun refresh() {
        when (currentAction) {
            DownloadFragment.Companion.ActionType.LOAD ->
                getDownloadedBooks(action = GetBooksActionType.SILENT_REFRESH)
            DownloadFragment.Companion.ActionType.SEARCH -> searchBooksLocal(currentKeyword)
        }
    }

    override fun unzipBook(context: Context?, book: BookLocal) {
        bookRepository.localWritableObj.unzipBook(context, book, object : OnResultListener<String> {
            override fun onSuccess(data: String) {
                mView?.onUnzipBookSuccess(book)
            }

            override fun onError(e: Exception?) {
                mView?.onUnzipBookSuccess(book)
                mView?.onError(e)
            }

        })
    }

    override fun parseEpubFile(book: BookLocal, providerUnzippedBookDirectoryPath: String) {
        bookRepository.localReadableObj.parseEpub(
            providerUnzippedBookDirectoryPath,
            book,
            object : OnResultListener<EpubFile> {
                override fun onSuccess(data: EpubFile) {
                    book.epubFile = data
                    mView?.onParseEpubDone(book.copy(prepared = 1), Result.SUCCESS)
                    /**Mark this book as prepared so that the UI can update*/
                }

                override fun onError(e: Exception?) {
                    mView?.onParseEpubDone(book, Result.FAILED)
                    mView?.onError(e)
                }

            })
    }

    override fun updateNewDataAndLoadMore(offset: Int) {
        bookRepository.localReadableObj.getBookWithLimit(
            offset + Constant.LOCAL_DATA_QUERY_PAGE_SIZE,
            object : OnResultListener<List<BookLocal>> {
                override fun onSuccess(data: List<BookLocal>) {
                    mView?.onGetBooksSuccess(
                        data.toBaseDataLocal(enableNextPage = true),
                        loadingArea = Constant.LoadingArea.Common.NOT_SHOW_LOADING,
                        action = GetBooksActionType.SILENT_REFRESH
                    )
                }

                override fun onError(e: Exception?) {
                    mView?.onError(e)
                }
            })
    }

    override fun setView(view: ListBookLocalContract.View?) {
        mView = view
    }
}
