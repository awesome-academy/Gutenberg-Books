package vn.ztech.software.projectgutenberg.screen.bookdetails

import android.app.DownloadManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.Agent
import vn.ztech.software.projectgutenberg.data.model.BaseData
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.repository.source.remote.api.APIQuery
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource
import vn.ztech.software.projectgutenberg.databinding.FragmentBookdetailsBinding
import vn.ztech.software.projectgutenberg.di.getListBookPresenter
import vn.ztech.software.projectgutenberg.screen.home.ListBookAdapter
import vn.ztech.software.projectgutenberg.screen.home.ListBookContract
import vn.ztech.software.projectgutenberg.screen.home.ListBookPresenter
import vn.ztech.software.projectgutenberg.screen.preview.PreviewFragment
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment
import vn.ztech.software.projectgutenberg.utils.extension.findCoverImageURL
import vn.ztech.software.projectgutenberg.utils.extension.findShowableAgent
import vn.ztech.software.projectgutenberg.utils.extension.getAvailableMetadata
import vn.ztech.software.projectgutenberg.utils.extension.getResourcesWithKind
import vn.ztech.software.projectgutenberg.utils.extension.listStatusShouldShow
import vn.ztech.software.projectgutenberg.utils.extension.loadImage
import vn.ztech.software.projectgutenberg.utils.extension.reasonToMsg
import vn.ztech.software.projectgutenberg.utils.extension.showAlertDialog
import vn.ztech.software.projectgutenberg.utils.extension.showSnackBar
import vn.ztech.software.projectgutenberg.utils.extension.statusToMsg
import vn.ztech.software.projectgutenberg.utils.extension.toast


class BookDetailsFragment
    : BaseFragment<FragmentBookdetailsBinding>(FragmentBookdetailsBinding::inflate),
    ListResourceAdapter.OnClickListener, ListBookContract.View {

    private lateinit var listBookPresenter: ListBookPresenter
    private var listBookAdapterSameAuthor = ListBookAdapter { book ->
        openFragment(
            BookDetailsFragment.newInstance(bundleOf(BUNDLE_BOOK to book))
        )
    }
    private var listBookAdapterSameBookshelf = ListBookAdapter { book ->
        openFragment(
            BookDetailsFragment.newInstance(bundleOf(BUNDLE_BOOK to book))
        )
    }
    private val listResourceAdapter = ListResourceAdapter(this)
    private var book: Book? = null
    private var showableAgent: Agent? = null
    private var selectedDownloadResource: Resource? = null
    private var currentStatus: Int? = null

    override fun initView(view: View) {

        arguments?.let {
            if (it.containsKey(BUNDLE_BOOK)) {
                book = it.getParcelable<Book>(BUNDLE_BOOK) as Book
            }
        }
        if (book == null) return

        val mBook = book!!
        binding?.apply {

            (activity as AppCompatActivity).apply {
                setSupportActionBar(layoutToolbar.toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                layoutToolbar.toolbar.title = mBook.title
            }

            mBook.resources.findCoverImageURL()?.let { ivBookCover.loadImage(it) }

            layoutBookDetailsInfo.apply {
                tvTitle.text = mBook.title
                mBook.agents.findShowableAgent()?.let {
                    tvAuthor.text =
                        String.format(resources.getString(R.string.format_author), it.person)

                    showableAgent = it
                }
                tvMetaData.text = mBook.getAvailableMetadata()
                if (!mBook.description.isNullOrEmpty() && mBook.description != Constant.STRING_NULL) {
                    tvDescriptionContent.text = mBook.description
                }

                recyclerListResource.adapter = listResourceAdapter
                listResourceAdapter.setData(mBook.resources.getResourcesWithKind())
            }

            layoutListBookHorizontalSameAuthor.recyclerListBooks.adapter = listBookAdapterSameAuthor
            listBookAdapterSameAuthor.loadMore(
                layoutListBookHorizontalSameAuthor.recyclerListBooks
            ) { page ->
                showableAgent?.let {
                    listBookPresenter.getBooksWithFilters(
                        page,
                        filters = APIQuery.getFiltersSameAuthor(it),
                        loadingArea = Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthorMore
                    )
                }
            }

            layoutListBookHorizontalSameBookshelf.recyclerListBooks.adapter =
                listBookAdapterSameBookshelf
            layoutListBookHorizontalSameBookshelf.tvTitle.text =
                resources.getString(R.string.same_bookshelf)
            listBookAdapterSameBookshelf.loadMore(
                layoutListBookHorizontalSameBookshelf.recyclerListBooks
            ) { page ->
                listBookPresenter.getBooksWithFilters(
                    page,
                    filters = mapOf(
                        BookDataSource.Companion.BookFilter.HAS_BOOKSHELF to mBook.bookshelves.first()
                    ),
                    loadingArea = Constant.LoadingAreaBookDetail.BookDetailsListWithSameBookshelfMore
                )
            }
        }

    }

    override fun initData() {
        activity?.let { listBookPresenter = getListBookPresenter(it.applicationContext) }
        listBookPresenter.setView(this)

        book?.let {
            showableAgent?.let { agent ->
                val filters = APIQuery.getFiltersSameAuthor(agent)
                listBookPresenter.getBooksWithFilters(
                    filters = filters,
                    loadingArea = Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthor,
                )
            }

            if (it.bookshelves.isNotEmpty()) {
                val filters = mapOf(
                    BookDataSource.Companion.BookFilter.HAS_BOOKSHELF to it.bookshelves.first()
                )
                listBookPresenter.getBooksWithFilters(
                    filters = filters,
                    loadingArea = Constant.LoadingAreaBookDetail.BookDetailsListWithSameBookshelf
                )
            }
        }
    }

    override fun onGetBooksSuccess(data: BaseData<Book>, loadingArea: Constant.LoadingArea) {
        if (loadingArea is Constant.LoadingAreaBookDetail) {
            when (loadingArea) {
                Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthor,
                Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthorMore,
                -> {
                    binding?.layoutListBookHorizontalSameAuthor?.tvCount?.text =
                        String.format(resources.getString(R.string.format_count), data.count)
                    listBookAdapterSameAuthor.setData(data)
                }
                Constant.LoadingAreaBookDetail.BookDetailsListWithSameBookshelf,
                Constant.LoadingAreaBookDetail.BookDetailsListWithSameBookshelfMore,
                -> {
                    binding?.layoutListBookHorizontalSameBookshelf?.tvCount?.text =
                        String.format(resources.getString(R.string.format_count), data.count)
                    listBookAdapterSameBookshelf.setData(data)
                }
            }
        }
    }

    override fun onGetRecentReadingBookSuccess(
        data: List<BookLocal>,
        loadingArea: Constant.LoadingArea
    ) {
        //todo leave blank
    }

    override fun onError(e: Exception?) {
        context?.toast(e.toString())
    }

    override fun updateLoading(loadingArea: Constant.LoadingArea, state: Constant.LoadingState) {
        val visibility = if (state == Constant.LoadingState.SHOW) View.VISIBLE else View.GONE
        if (loadingArea is Constant.LoadingAreaBookDetail) {
            when (loadingArea) {
                Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthor -> {
                    binding?.layoutListBookHorizontalSameAuthor?.loadingView?.layoutLoading?.visibility =
                        visibility
                }
                Constant.LoadingAreaBookDetail.BookDetailsListWithSameAuthorMore -> {
                    binding?.layoutListBookHorizontalSameAuthor?.iconLoadingEnd?.visibility =
                        visibility
                }
                Constant.LoadingAreaBookDetail.BookDetailsListWithSameBookshelf -> {
                    binding?.layoutListBookHorizontalSameBookshelf?.loadingView?.layoutLoading?.visibility =
                        visibility
                }
                Constant.LoadingAreaBookDetail.BookDetailsListWithSameBookshelfMore -> {
                    binding?.layoutListBookHorizontalSameBookshelf?.iconLoadingEnd?.visibility =
                        visibility
                }
            }
        }
    }

    override fun onItemClick(resource: Resource) {
        when (resource.actionType) {
            Constant.ActionTypes.PREVIEW -> {
                openFragment(
                    PreviewFragment.newInstance(bundleOf(BUNDLE_BOOK_PREVIEW_URL to resource.uri))
                )
            }
            Constant.ActionTypes.DOWNLOAD -> {
                selectedDownloadResource = resource
                context?.showAlertDialog(
                    resources.getString(R.string.dialog_msg_download_confirm_title),
                    String.format(
                        resources.getString(R.string.dialog_msg_download_confirm_description),
                        book?.title
                    ),
                    onClickOkListener = { _, _ ->
                        handleDownloadBook(
                            activity,
                            binding,
                            Pair(selectedDownloadResource!!, book!!),
                            ::handleDownloadResponse,
                            ::onError
                        )
                    },
                )
            }
            else -> {
                /** Do nothing */
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE.hashCode() -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (selectedDownloadResource != null && book != null) {
                        downloadBook(
                            activity,
                            binding,
                            Pair(selectedDownloadResource!!, book!!),
                            ::handleDownloadResponse,
                            ::onError
                        )
                    }
                }
                return
            }
        }
    }


    private fun handleDownloadResponse(status: Int, reason: Int) {
        if (status == currentStatus) return

        currentStatus = status
        if (status in listStatusShouldShow) {
            binding?.root?.let {
                when (status) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        context?.showSnackBar(
                            it,
                            status.statusToMsg() +
                                    if (reason != 0) "\nReason: ${reason.reasonToMsg()}" else "",
                            resources.getString(R.string.view)
                        ) {
                            /**Move to download fragment*/
                            moveToTab(R.id.menu_download)
                        }
                    }
                    else -> {
                        context?.showSnackBar(
                            it,
                            status.statusToMsg() +
                                    if (reason != 0) "\nReason: ${reason.reasonToMsg()}" else "",
                            resources.getString(R.string.OK)
                        ) {}
                        context?.toast((status.statusToMsg() +
                                if (reason != 0) "\nReason: ${reason.reasonToMsg()}" else ""))
                    }
                }

            }
        }
    }

    companion object {
        fun newInstance(bundle: Bundle? = null): BookDetailsFragment {
            val fragment = BookDetailsFragment().apply {
                arguments = bundle
            }
            return fragment
        }

        const val BUNDLE_BOOK = "BUNDLE_BOOK"
        const val BUNDLE_BOOK_PREVIEW_URL = "BUNDLE_BOOK_PREVIEW_URL"

    }
}
