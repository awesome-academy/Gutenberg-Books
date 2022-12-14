package vn.ztech.software.projectgutenberg.utils.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewBinding>(private val inflate: (LayoutInflater) -> T) :
    AppCompatActivity() {

    var binding: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate.invoke(layoutInflater)
        binding?.let {
            setContentView(it.root)
            initView()
            initData()
        }
    }

    abstract fun initView()
    abstract fun initData()

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}
