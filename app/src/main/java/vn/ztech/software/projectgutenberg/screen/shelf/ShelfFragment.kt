package vn.ztech.software.projectgutenberg.screen.shelf

import android.view.View
import vn.ztech.software.projectgutenberg.databinding.FragmentShelfBinding
import vn.ztech.software.projectgutenberg.utils.base.BaseFragment

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
