package vn.ztech.software.projectgutenberg.screen.download

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.BaseDataLocal
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.getProviderUnzippedBookDirectoryPath
import vn.ztech.software.projectgutenberg.databinding.FragmentDownloadBinding
import vn.ztech.software.projectgutenberg.di.getListLocalBookPresenter
import vn.ztech.software.projectgutenberg.screen.readbook.ReadBookActivity
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.Constant.LoadingAreaDownloadedBook
import vn.ztech.software.projectgutenberg.utils.base.BaseAdapterLocal
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment
import vn.ztech.software.projectgutenberg.utils.base.BasePresenter.Companion.Result
import vn.ztech.software.projectgutenberg.utils.extension.checkPermissions
import vn.ztech.software.projectgutenberg.utils.extension.removeUnderScore
import vn.ztech.software.projectgutenberg.utils.extension.showAlertDialog
import vn.ztech.software.projectgutenberg.utils.extension.toast


class DownloadFragment
    : BaseFragment<FragmentDownloadBinding>(FragmentDownloadBinding::inflate),
    ListBookLocalAdapter.OnClickListener {
    private val mView = object : ListBookLocalContract.View {
        override fun onGetBooksSuccess(
            data: BaseDataLocal<BookLocal>,
            loadingArea: Constant.LoadingArea,
            action: GetBooksActionType
        ) {
            when (action) {
                GetBooksActionType.LOAD -> {
                    listBookLocalAdapter.setData(data, BaseAdapterLocal.DataAction.ADD)
                }
                GetBooksActionType.REFRESH,
                GetBooksActionType.SILENT_REFRESH -> {
                    listBookLocalAdapter.setData(data, BaseAdapterLocal.DataAction.REPLACE)
                }
            }
            listBookLocalAdapter.loadDone()
        }

        override fun onScanBookComplete(data: Boolean, loadingArea: Constant.LoadingArea) {
            isFetchingFromStorage = false
            if (data) {
                listBookLocalPresenter.getDownloadedBooks(action = GetBooksActionType.REFRESH)
            } else {
                val msg = context?.getString(R.string.msg_scan_book_failed)
                msg?.let { context?.toast(it) }
            }
        }

        override fun onError(e: Exception?) {
            context?.toast(e?.message.toString())
        }

        override fun onHitLastPage(offset: Int) {
            listBookLocalAdapter.setLastItem(offset)
        }

        override fun onDeleteBookComplete(data: Boolean) {
            val msg = context?.getString(
                if (data) R.string.msg_delete_book_success
                else R.string.msg_delete_book_failed
            )
            msg?.let { context?.toast(msg) }
            listBookLocalPresenter.refresh()
        }

        override fun setLoadMore(enable: Boolean) {
            listBookLocalAdapter.setLoadMore(enable)
        }

        override fun onUnzipBookSuccess(book: BookLocal) {
//            listBookLocalAdapter.setLoading(book, isLoading = false)
            context?.let {
                listBookLocalPresenter.parseEpubFile(
                    book,
                    getProviderUnzippedBookDirectoryPath(book.title)
                )
            }
        }

        override fun onUnzipBookFailed(book: BookLocal) {
            listBookLocalAdapter.setLoading(book, isLoading = false)
        }

        override fun onParseEpubDone(book: BookLocal, result: Result) {
            val msg = context?.getString(
                if (result == Result.SUCCESS) R.string.msg_parse_epub_success
                else R.string.msg_parse_epub_failed
            )
            msg?.let { context?.toast(it) }
            listBookLocalAdapter.setLoading(book, isLoading = false)
        }

        override fun updateLoading(
            loadingArea: Constant.LoadingArea,
            state: Constant.LoadingState
        ) {
            val visibility = if (state == Constant.LoadingState.SHOW) View.VISIBLE else View.GONE
            if (visibility == View.GONE) binding?.swipeRefreshLayout?.isRefreshing = false

            if (loadingArea is LoadingAreaDownloadedBook) {
                when (loadingArea) {
                    LoadingAreaDownloadedBook.DownloadedBookMain -> {
                        binding?.layoutListDownloadedBooks?.loadingView?.layoutLoading?.visibility =
                            visibility
                    }
                    LoadingAreaDownloadedBook.DownloadedBookLoadMore -> {
                        binding?.viewBottomLoading?.bottomLoadingView?.visibility = visibility
                    }
                }
            }
        }

    }
    lateinit var listBookLocalPresenter: ListBookLocalPresenter
    private var isFetchingFromStorage = false
    private val listBookLocalAdapter by lazy { ListBookLocalAdapter(this) }
    override fun initView(view: View) {
        activity?.let {
            listBookLocalPresenter = getListLocalBookPresenter(it.applicationContext)
        }
        binding?.apply {
            swipeRefreshLayout.setOnRefreshListener {
                context?.showAlertDialog(
                    getString(R.string.title_fetch_data_from_storage),
                    getString(R.string.msg_fetch_data_from_storage),
                    onClickOkListener = { _, _ ->
                        fetchDataFromLocal()
                    },
                    onClickCancelListener = { _, _ ->
                        binding?.swipeRefreshLayout?.isRefreshing = false
                    }
                )
            }

            layoutListDownloadedBooks.recyclerListBooks.adapter = listBookLocalAdapter
            listBookLocalAdapter.loadMore(layoutListDownloadedBooks.recyclerListBooks) { offset ->
                listBookLocalPresenter.getDownloadedBooks(
                    offset,
                    LoadingAreaDownloadedBook.DownloadedBookLoadMore
                )
            }
            listBookLocalAdapter.mSetUpdateNewData { offset ->
                listBookLocalPresenter.updateNewDataAndLoadMore(offset)
            }

            topAppBarHome.editTextSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftInput(requireContext(), v)
                    performSearch(v.text.toString())
                    true
                } else {
                    false
                }
            }
            topAppBarHome.textInputLayoutSearch.setStartIconOnClickListener {
                hideSoftInput(requireContext(), it)
                topAppBarHome.editTextSearch.text.toString().let { keyword ->
                    performSearch(keyword)
                }
            }
        }
        listBookLocalPresenter.setView(mView)
    }

    private fun performSearch(keyword: String) {
        listBookLocalPresenter.searchBooksLocal(keyword)
    }

    override fun initData() {
        listBookLocalPresenter.getDownloadedBooks(loadingArea = LoadingAreaDownloadedBook.DownloadedBookMain)
    }

    private fun fetchDataFromLocal() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                /** Request permission: access all files*/
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", context?.packageName, null)
                intent.data = uri
                startActivity(intent)
                isFetchingFromStorage = true
            } else {
                context?.let {
                    listBookLocalPresenter.scanLocalStorage(
                        it,
                        LoadingAreaDownloadedBook.DownloadedBookMain
                    )
                }
                isFetchingFromStorage = false
            }
        } else {
            /**For below android 11*/
            activity?.checkPermissions(permissions = arrayOf(WRITE_EXTERNAL_STORAGE)) {
                context?.let {
                    listBookLocalPresenter.scanLocalStorage(
                        it,
                        LoadingAreaDownloadedBook.DownloadedBookMain
                    )
                }
                isFetchingFromStorage = false
            }
            isFetchingFromStorage = true
        }
    }

    override fun onItemClick(book: BookLocal) {
        if (book.prepared == BookLocal.PREPARED) {
            val intent = Intent(activity, ReadBookActivity::class.java)
            intent.putExtras(bundleOf(ReadBookActivity.BUNDLE_BOOK_LOCAL to book))
            startActivity(intent)
        } else {
            if (book.isSupported()){
                context?.let {
                    it.showAlertDialog(
                        resources.getString(R.string.read_book_title),
                        resources.getString(R.string.read_book_msg),
                        onClickOkListener = { _, _ ->
                            listBookLocalAdapter.setLoading(book)
                            listBookLocalPresenter.unzipBook(context, book)
                        },
                        onClickCancelListener = { _, _ -> }
                    )
                }
            }else{
                context?.let {
                    it.showAlertDialog(
                        resources.getString(R.string.title_not_supported_file_extension),
                        String.format(resources.getString(R.string.msg_not_supported_file_extension), book.mimeType),
                    )
                }
            }

        }

    }

    override fun onDeleteButtonClicked(book: BookLocal) {
        context?.let {
            it.showAlertDialog(
                resources.getString(R.string.delete_book_local_title),
                String.format(
                    resources.getString(R.string.delete_book_local_msg),
                    book.title.removeUnderScore()
                ),
                onClickOkListener = { _, _ -> listBookLocalPresenter.deleteBookLocal(it, book) },
                onClickCancelListener = { _, _ -> }
            )
        }
    }

    override fun onStop() {
        super.onStop()
        binding?.swipeRefreshLayout?.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        if (isFetchingFromStorage) {
            fetchDataFromLocal()
        }
        listBookLocalAdapter.updateNewDataAndLoadMore()
    }

    companion object {
        fun newInstance() = DownloadFragment()
        enum class GetBooksActionType {
            REFRESH,
            LOAD,
            SILENT_REFRESH
        }

        enum class ActionType {
            SEARCH,
            LOAD
        }
    }


}
