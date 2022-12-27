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
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.BaseDataLocal
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.repository.source.local.BookLocalDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.BookDbHelper
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookDbDAO
import vn.ztech.software.projectgutenberg.data.repository.source.remote.BookRemoteDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookRepository
import vn.ztech.software.projectgutenberg.databinding.FragmentDownloadBinding
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.Constant.LoadingAreaDownloadedBook
import vn.ztech.software.projectgutenberg.utils.base.BaseAdapterLocal
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment
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
                context?.toast(getString(R.string.msg_scan_book_failed))
            }
        }

        override fun onError(e: Exception?) {
            context?.toast(e?.message.toString())
        }

        override fun onHitLastPage(offset: Int) {
            listBookLocalAdapter.setLastItem(offset)
        }

        override fun onDeleteBookComplete(data: Boolean) {
            var msg = ""
            when (data) {
                true -> {
                    msg = getString(R.string.msg_delete_book_success)
                }
                false -> {
                    msg = getString(R.string.msg_delete_book_success)
                }
            }
            context?.toast(msg)
            listBookLocalPresenter.refresh()
        }

        override fun setLoadMore(enable: Boolean) {
            listBookLocalAdapter.setLoadMore(enable)
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
    private val listBookLocalPresenter
            by lazy {
                ListBookLocalPresenter(
                    BookRepository.getInstance(
                        BookRemoteDataSource.getInstance(),
                        BookLocalDataSource.getInstance(
                            BookDbDAO.getInstance(
                                BookDbHelper.getInstance(context)
                            )
                        )
                    )
                )
            }
    private var isFetchingFromStorage = false
    private val listBookLocalAdapter by lazy { ListBookLocalAdapter(this) }

    override fun initView(view: View) {
        binding?.apply {
            swipeRefreshLayout.setOnRefreshListener {
                context?.showAlertDialog(
                    getString(R.string.title_fetch_data_from_storage),
                    getString(R.string.msg_fetch_data_from_storage),
                    onClickOkListener = { _, _ ->
                        fetchDataFromLocal()
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
        //TODO implement later
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
        listBookLocalAdapter.forceLoadMore()
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
