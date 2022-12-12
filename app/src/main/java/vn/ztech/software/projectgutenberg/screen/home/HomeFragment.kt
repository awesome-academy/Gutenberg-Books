package vn.ztech.software.projectgutenberg.screen.home

import android.view.View
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.BookRepository
import vn.ztech.software.projectgutenberg.data.repository.source.local.BookLocalDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.remote.BookRemoteDataSource
import vn.ztech.software.projectgutenberg.databinding.FragmentHomeBinding
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment
import vn.ztech.software.projectgutenberg.utils.extension.toast
import java.lang.Exception

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), HomeContract.View {
    private lateinit var homePresenter: HomePresenter

    override fun initView(view: View) {
        //TODO Implement later
    }

    override fun initData() {
        homePresenter = HomePresenter(
            BookRepository.getInstance(
                BookRemoteDataSource.getInstance(),
                BookLocalDataSource.getInstance()
            )
        )
        homePresenter.setView(this)
        homePresenter.getBooks()
    }

    override fun onGetBooksSuccess(books: List<Book>) {
        context?.toast(mutableListOf<Book>(Book("War and peace")).toString())
    }

    override fun onError(e: Exception?) {
        //TODO show error
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
