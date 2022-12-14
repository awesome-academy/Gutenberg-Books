package vn.ztech.software.projectgutenberg.utils.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.manager.SupportRequestManagerFragment
import vn.ztech.software.projectgutenberg.screen.MainActivity
import vn.ztech.software.projectgutenberg.screen.bookshelf.BookshelfFragment
import vn.ztech.software.projectgutenberg.screen.download.DownloadFragment
import vn.ztech.software.projectgutenberg.screen.favorite.FavoriteFragment
import vn.ztech.software.projectgutenberg.screen.home.HomeFragment
import vn.ztech.software.projectgutenberg.utils.extension.addFragment

abstract class BaseFragment<T : ViewBinding>(private val inflate: (LayoutInflater) -> T) :
    Fragment() {

    protected var binding: T? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate.invoke(inflater)
        binding?.let {
            initView(it.root)
        }
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    abstract fun initView(view: View)
    abstract fun initData()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun hideSoftInput(context: Context, v: View) {
        v.clearFocus()
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun openFragment(fragment: Fragment) {
        this.addFragment(
            fragment
        )
    }

    fun moveToTab(@IdRes menuId: Int) {
        clearFragments()
        (activity as MainActivity?)?.binding?.bottomNavigationView?.selectedItemId =
            menuId
    }

    private fun clearFragments() {
        activity?.supportFragmentManager?.fragments?.let {

            for (fragment in it) {
                if (!(fragment::class.java in fragmentsShouldNotClear)) {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(fragment)?.commit()
                }
            }
        }

    }

    fun isConnected(): Boolean {
        try {
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                val cm =
                    context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkCapabilities = cm.getNetworkCapabilities(cm.activeNetwork)
                networkCapabilities != null
            } else {
                val cm = context?.getSystemService(
                    Context.CONNECTIVITY_SERVICE
                ) as ConnectivityManager
                val networkInfo = cm.activeNetworkInfo
                networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
            }

        } catch (e: java.lang.RuntimeException) {
            Log.e("Connectivity Exception", e.message!!)
            return false
        }
    }

    companion object {
        val fragmentsShouldNotClear = listOf<Class<*>>(
            HomeFragment::class.java,
            BookshelfFragment::class.java,
            DownloadFragment::class.java,
            FavoriteFragment::class.java,
            SupportRequestManagerFragment::class.java
        )
    }

}
