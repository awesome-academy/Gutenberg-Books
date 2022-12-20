package vn.ztech.software.projectgutenberg.screen.bookdetails

import android.os.Bundle
import android.view.View
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.databinding.FragmentBookdetailsBinding
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment

class BookdetailsFragment
    : BaseFragment<FragmentBookdetailsBinding>(FragmentBookdetailsBinding::inflate) {
    override fun initView(view: View) {
        var book: Book? = null
        arguments?.let {
            if (it.containsKey(BUNDLE_BOOK)) {
                book = it.getParcelable<Book>(BUNDLE_BOOK) as Book
            }
        }
        binding?.tvTest?.text = book.toString()
    }

    override fun initData() {
        //TODO Implement later
    }

    companion object {
        fun newInstance(bundle: Bundle? = null): BookdetailsFragment {
            val fragment = BookdetailsFragment().apply {
                arguments = bundle
            }
            return fragment
        }

        const val BUNDLE_BOOK = "BUNDLE_BOOK"
    }
}
