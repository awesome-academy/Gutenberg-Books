package vn.ztech.software.projectgutenberg.screen.booksearch

import android.os.Bundle
import android.view.View
import vn.ztech.software.projectgutenberg.databinding.FragmentBookSearchBinding
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment

class BookSearchFragment
    : BaseFragment<FragmentBookSearchBinding>(FragmentBookSearchBinding::inflate) {
    override fun initView(view: View) {
        var bookSearchKeyWord: String? = null
        arguments?.let {
            if (it.containsKey(BUNDLE_BOOK_SEARCH)) {
                bookSearchKeyWord = it.getString(BUNDLE_BOOK_SEARCH) as String
            }
        }
        binding?.tvTest?.text = bookSearchKeyWord.toString()
    }

    override fun initData() {
        //TODO Implement later
    }

    companion object {
        fun newInstance(bundle: Bundle? = null): BookSearchFragment {
            val fragment = BookSearchFragment().apply {
                arguments = bundle
            }
            return fragment
        }

        const val BUNDLE_BOOK_SEARCH = "BUNDLE_BOOK_SEARCH"
    }
}
