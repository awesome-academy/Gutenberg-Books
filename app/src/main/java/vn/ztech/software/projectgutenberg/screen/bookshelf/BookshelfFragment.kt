package vn.ztech.software.projectgutenberg.screen.bookshelf

import android.os.Bundle
import android.view.View
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.databinding.FragmentBookshelfBinding
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment

class BookshelfFragment
    : BaseFragment<FragmentBookshelfBinding>(FragmentBookshelfBinding::inflate) {
    override fun initView(view: View) {
        var book: Bookshelf? = null
        arguments?.let {
            if (it.containsKey(BUNDLE_BOOKSHELF)) {
                book = it.getParcelable<Bookshelf>(BUNDLE_BOOKSHELF) as Bookshelf
            }
        }
        book?.let { binding?.tvTest?.text = book.toString() }
    }

    override fun initData() {
        //TODO Implement later
    }

    companion object {
        fun newInstance(bundle: Bundle? = null): BookshelfFragment {
            val fragment = BookshelfFragment().apply {
                arguments = bundle
            }
            return fragment
        }

        const val BUNDLE_BOOKSHELF = "BUNDLE_BOOKSHELF"
    }
}
