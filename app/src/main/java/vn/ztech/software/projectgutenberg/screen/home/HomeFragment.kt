package vn.ztech.software.projectgutenberg.screen.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.BaseData
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.data.repository.source.remote.BookshelfRemoteDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.repository.bookshelf.BookshelfRepository
import vn.ztech.software.projectgutenberg.databinding.FragmentHomeBinding
import vn.ztech.software.projectgutenberg.di.getListBookPresenter
import vn.ztech.software.projectgutenberg.screen.bookdetails.BookDetailsFragment
import vn.ztech.software.projectgutenberg.screen.booksearch.BookSearchFragment
import vn.ztech.software.projectgutenberg.screen.bookshelf.BookshelfFragment
import vn.ztech.software.projectgutenberg.screen.readbook.ReadBookActivity
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment
import vn.ztech.software.projectgutenberg.utils.extension.toast
import java.io.IOException


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    View.OnClickListener {

    private lateinit var listBookPresenter: ListBookPresenter
    private lateinit var listBookshelfPresenter: ListBookshelfPresenter
    private var listBookAdapter: ListBookAdapter = ListBookAdapter{ book ->
        openFragment(
            BookDetailsFragment.newInstance(
                bundleOf(BookDetailsFragment.BUNDLE_BOOK to book)
            )
        )
    }
    private var listBookShelfAdapter: ListBookshelvesAdapter = ListBookshelvesAdapter{ bookshelf ->
        openFragment(
            BookshelfFragment.newInstance(
                bundleOf(BookshelfFragment.BUNDLE_BOOKSHELF to bookshelf)
            )
        )
    }
    private var listRecentReadingBookAdapter: ListRecentReadingBooksAdapter =
        ListRecentReadingBooksAdapter { book ->
            val intent = Intent(activity, ReadBookActivity::class.java)
            intent.putExtras(bundleOf(ReadBookActivity.BUNDLE_BOOK_LOCAL to book))
            startActivity(intent)
        }
    private val connectivityManager
        get() = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    private val onScrollChangedListener = object : NestedRecyclerViewPaginator() {
        override fun onHitBottom(page: Int) {
            listBookPresenter.getBooks(page, Constant.LoadingAreaHome.HomeLoadMoreBook)
        }
    }

    private val mListBookView = object : ListBookContract.View{

        override fun onGetBooksSuccess(data: BaseData<Book>, loadingArea: Constant.LoadingArea) {
            listBookAdapter.setData(data)
            onScrollChangedListener.onLoadDone()
        }

        override fun onGetRecentReadingBookSuccess(
            data: List<BookLocal>,
            loadingArea: Constant.LoadingArea
        ) {
            listRecentReadingBookAdapter.setData(data)
        }


        override fun onError(e: Exception?) {
            this@HomeFragment.onError(e)
        }

        override fun updateLoading(
            loadingArea: Constant.LoadingArea,
            state: Constant.LoadingState
        ) {
            this@HomeFragment.updateLoading(loadingArea, state)
        }
    }

    private val mListBookshelfView = object : ListBookshelfContract.View{
        override fun onGetBookshelvesSuccess(bookshelves: List<Bookshelf>) {
            listBookShelfAdapter.setData(bookshelves)
        }

        override fun onError(e: Exception?) {
            this@HomeFragment.onError(e)
        }

        override fun updateLoading(
            loadingArea: Constant.LoadingArea,
            state: Constant.LoadingState
        ) {
            this@HomeFragment.updateLoading(loadingArea, state)
        }

    }

    override fun initView(view: View) {
        if (!isConnected()) {
            binding?.layoutInternetLost?.layoutNetworkLost?.post {
                binding?.layoutInternetLost?.layoutNetworkLost?.visibility = View.VISIBLE
            }
            binding?.layoutSwipeRefresh?.isEnabled = false
        }

        binding?.apply {
            layoutListBooks.recyclerListBooks.apply {
                adapter = listBookAdapter
            }
            layoutListRecentReading.recylerListRecentReadingBooks.adapter =
                listRecentReadingBookAdapter
            nested.setOnScrollChangeListener(onScrollChangedListener)
            layoutListBookshelves.recyclerBookshelves.adapter = listBookShelfAdapter
            layoutListBookshelves.btViewMoreBookshelf.setOnClickListener(this@HomeFragment)

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
                    if (keyword.isNotEmpty()) performSearch(keyword)
                }
            }

            layoutSwipeRefresh.setOnRefreshListener {
                initData()
            }

            layoutInternetLost.btReadBookOffline.setOnClickListener {
                moveToTab(R.id.menu_download)
            }
        }

    }

    private fun performSearch(keyword: String) {
        openFragment(
            BookSearchFragment.newInstance(
                bundleOf(BookSearchFragment.BUNDLE_BOOK_SEARCH to keyword)
            )
        )
    }

    override fun initData() {
        activity?.let { listBookPresenter = getListBookPresenter(it.applicationContext) }
        listBookPresenter.setView(mListBookView)
        listBookPresenter.getBooks()
        listBookPresenter.getAllRecentReadingBook()

        listBookshelfPresenter = ListBookshelfPresenter(
            BookshelfRepository.getInstance(
                BookshelfRemoteDataSource.getInstance(),
            )
        )
        listBookshelfPresenter.setView(mListBookshelfView)
        listBookshelfPresenter.getBookshelves()
    }

    fun onError(e: Exception?) {
        context?.toast(e.toString())
    }

    fun updateLoading(loadingArea: Constant.LoadingArea, state: Constant.LoadingState) {
        val visibility = if (state == Constant.LoadingState.SHOW) View.VISIBLE else View.GONE
        if (visibility == View.GONE) binding?.layoutSwipeRefresh?.isRefreshing = false

        if (loadingArea is Constant.LoadingAreaHome) {
            when (loadingArea) {
                Constant.LoadingAreaHome.HomeRecentReading -> {
                    binding?.layoutListRecentReading?.loadingView?.layoutLoading?.visibility =
                        visibility
                }
                Constant.LoadingAreaHome.HomeBookshelf -> {
                    binding?.layoutListBookshelves?.loadingView?.layoutLoading?.visibility =
                        visibility
                }
                Constant.LoadingAreaHome.HomeListBook -> {
                    binding?.layoutListBooks?.loadingView?.layoutLoading?.visibility = visibility
                }
                Constant.LoadingAreaHome.HomeLoadMoreBook -> {
                    binding?.viewBottomLoading?.bottomLoadingView?.visibility = visibility
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bt_view_more_bookshelf -> {
                openFragment(fragment = BookshelfFragment.newInstance())
            }
            R.id.bt_view_more_recent_reading -> {
                // TODO Implement later
            }
        }
    }

    private fun netWorkStateObj() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            binding?.layoutInternetLost?.layoutNetworkLost?.post {
                binding?.layoutInternetLost?.layoutNetworkLost?.visibility = View.GONE
                binding?.layoutSwipeRefresh?.isEnabled = true
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            binding?.layoutInternetLost?.layoutNetworkLost?.post {
                binding?.layoutInternetLost?.layoutNetworkLost?.visibility = View.VISIBLE
                binding?.layoutSwipeRefresh?.isEnabled = false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        connectivityManager?.registerNetworkCallback(networkRequest(), netWorkStateObj())
        listBookPresenter.getAllRecentReadingBook(state = Constant.LoadingState.HIDE)
    }

    override fun onStop() {
        super.onStop()
        try {
            connectivityManager?.unregisterNetworkCallback(netWorkStateObj())
        } catch (e: java.lang.IllegalArgumentException) {
            // todo network callback has already been unregistered, no need to unregister
            Log.d("Error", e.message.toString())
        }
        binding?.layoutSwipeRefresh?.isRefreshing = false
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    fun networkRequest() = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

}
