package vn.ztech.software.projectgutenberg.screen.preview

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import vn.ztech.software.projectgutenberg.databinding.FragmentPreviewBinding
import vn.ztech.software.projectgutenberg.screen.bookdetails.BookDetailsFragment
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment


class PreviewFragment
    : BaseFragment<FragmentPreviewBinding>(FragmentPreviewBinding::inflate) {
    private var url: String? = null
    override fun initView(view: View) {
        arguments?.let {
            if (it.containsKey(BookDetailsFragment.BUNDLE_BOOK_PREVIEW_URL)) {
                url = it.getString(BookDetailsFragment.BUNDLE_BOOK_PREVIEW_URL)
            }
        }

        binding?.layoutLoadingView?.layoutLoading?.visibility = View.VISIBLE
        binding?.webViewPreview?.loadUrl(url ?: "")
        binding?.webViewPreview?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                binding?.layoutLoadingView?.layoutLoading?.visibility = View.GONE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding?.layoutLoadingView?.layoutLoading?.visibility = View.GONE
            }
        }
    }

    override fun initData() {
        //TODO Implement later
    }

    companion object {
        fun newInstance(bundle: Bundle? = null): PreviewFragment {
            val fragment = PreviewFragment().apply {
                arguments = bundle
            }
            return fragment
        }
    }
}
