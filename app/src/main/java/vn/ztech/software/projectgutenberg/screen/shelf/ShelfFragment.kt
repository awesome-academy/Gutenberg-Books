package vn.ztech.software.projectgutenberg.screen.shelf

import android.view.View
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.BookRepository
import vn.ztech.software.projectgutenberg.data.repository.source.local.BookLocalDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.remote.BookRemoteDataSource
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment
import vn.ztech.software.projectgutenberg.databinding.FragmentHomeBinding
import vn.ztech.software.projectgutenberg.databinding.FragmentShelfBinding
import vn.ztech.software.projectgutenberg.utils.extension.toast
import java.lang.Exception

class ShelfFragment :
    BaseFragment<FragmentShelfBinding>(FragmentShelfBinding::inflate)/*, HomeContract.View*/ {

    override fun initView(view: View) {
        //TODO Implement later
    }

    override fun initData() {
        //TODO Implement later
    }

    companion object {
        fun newInstance() = ShelfFragment()
    }
}
