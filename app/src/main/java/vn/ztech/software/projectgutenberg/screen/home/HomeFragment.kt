package vn.ztech.software.projectgutenberg.screen.home

import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.BaseData
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.data.repository.source.remote.BookshelfRemoteDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.repository.bookshelf.BookshelfRepository
import vn.ztech.software.projectgutenberg.databinding.FragmentHomeBinding
import vn.ztech.software.projectgutenberg.di.getListBookPresenter
import vn.ztech.software.projectgutenberg.screen.bookdetails.BookDetailsFragment
import vn.ztech.software.projectgutenberg.screen.booksearch.BookSearchFragment
import vn.ztech.software.projectgutenberg.screen.bookshelf.BookshelfFragment
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment
import vn.ztech.software.projectgutenberg.utils.extension.toast

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate),
    ListBookContract.View, ListBookshelfContract.View, ListBookAdapter.OnClickListener,
    ListBookshelvesAdapter.OnClickListener, View.OnClickListener {

    private lateinit var listBookPresenter: ListBookPresenter
    private lateinit var listBookshelfPresenter: ListBookshelfPresenter
    private var listBookAdapter: ListBookAdapter = ListBookAdapter(this)
    private var listBookShelfAdapter: ListBookshelvesAdapter = ListBookshelvesAdapter(this)

    private val onScrollChangedListener = object : NestedRecyclerViewPaginator() {
        override fun onHitBottom(page: Int) {
            listBookPresenter.getBooks(page, Constant.LoadingAreaHome.HomeLoadMoreBook)
        }
    }

    override fun initView(view: View) {
        binding?.apply {
            layoutListBooks.recyclerListBooks.apply {
                adapter = listBookAdapter
            }
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
        listBookPresenter.setView(this)
        listBookPresenter.getBooks()

        listBookshelfPresenter = ListBookshelfPresenter(
            BookshelfRepository.getInstance(
                BookshelfRemoteDataSource.getInstance(),
            )
        )
        listBookshelfPresenter.setView(this)
        listBookshelfPresenter.getBookshelves()
    }

    override fun onGetBooksSuccess(data: BaseData<Book>, loadingArea: Constant.LoadingArea) {
        listBookAdapter.setData(data)
        onScrollChangedListener.onLoadDone()
    }

    override fun onGetBookshelvesSuccess(bookshelves: List<Bookshelf>) {
        listBookShelfAdapter.setData(bookshelves)
    }

    override fun onError(e: Exception?) {
        requireContext().toast(e.toString())
    }

    override fun updateLoading(loadingArea: Constant.LoadingArea, state: Constant.LoadingState) {
        val visibility = if (state == Constant.LoadingState.SHOW) View.VISIBLE else View.GONE
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

    override fun onItemClick(book: Book) {
        openFragment(
            BookDetailsFragment.newInstance(
                bundleOf(BookDetailsFragment.BUNDLE_BOOK to book)
            )
        )
    }

    override fun onItemClick(bookshelf: Bookshelf) {
        openFragment(
            BookshelfFragment.newInstance(
                bundleOf(BookshelfFragment.BUNDLE_BOOKSHELF to bookshelf)
            )
        )
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

    companion object {
        fun newInstance() = HomeFragment()
    }

}
