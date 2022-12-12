package vn.ztech.software.projectgutenberg.screen

import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.databinding.ActivityMainBinding
import vn.ztech.software.projectgutenberg.screen.home.HomeFragment
import vn.ztech.software.projectgutenberg.utils.base.BaseActivity

class MainActivity: BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun initView() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, HomeFragment.newInstance())
            .addToBackStack(this::class.qualifiedName)
            .commit()
    }

    override fun initData() {
        //TODO Implement later
    }
}
