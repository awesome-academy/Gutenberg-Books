package vn.ztech.software.projectgutenberg.utils.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
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

}
