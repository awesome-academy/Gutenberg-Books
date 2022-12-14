package vn.ztech.software.projectgutenberg.screen

import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.navigation.NavigationBarView
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.databinding.ActivityMainBinding
import vn.ztech.software.projectgutenberg.screen.bookshelf.BookshelfFragment
import vn.ztech.software.projectgutenberg.screen.download.DownloadFragment
import vn.ztech.software.projectgutenberg.screen.favorite.FavoriteFragment
import vn.ztech.software.projectgutenberg.screen.home.HomeFragment
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.ConstantJava
import vn.ztech.software.projectgutenberg.utils.base.BaseActivity
import vn.ztech.software.projectgutenberg.utils.extension.showAlertDialog

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),
    NavigationBarView.OnItemSelectedListener {

    private val pagerAdapter = PagerAdapter(this)

    override fun initView() {
        binding?.bottomNavigationView?.setOnItemSelectedListener(this)
        binding?.pager?.adapter = pagerAdapter
        /** Disable swipe behavior for view pager */
        binding?.pager?.isUserInputEnabled = false
        binding?.pager?.offscreenPageLimit = ConstantJava.OFF_LIMIT_SCREEN_NUMBER
    }

    override fun initData() {
        //TODO Implement later
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_home -> {
                binding?.pager?.currentItem = Constant.ScreenNumber.Home.ordinal
                true
            }
            R.id.menu_bookshelf -> {
                binding?.pager?.currentItem = Constant.ScreenNumber.Shelf.ordinal
                true
            }
            R.id.menu_download -> {
                binding?.pager?.currentItem = Constant.ScreenNumber.Download.ordinal
                true
            }
            R.id.menu_favorite -> {
                binding?.pager?.currentItem = Constant.ScreenNumber.Favorite.ordinal
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            super.onBackPressed()
        } else {
            this.showAlertDialog(
                R.string.dialog_title_quit_app,
                R.string.dialog_message_quit_app,
                onClickOkListener = { _, _ -> finish() }
            )
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    inner class PagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return Constant.NUM_SCREEN
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                Constant.ScreenNumber.Home.ordinal -> HomeFragment.newInstance()
                Constant.ScreenNumber.Shelf.ordinal -> BookshelfFragment.newInstance()
                Constant.ScreenNumber.Download.ordinal -> DownloadFragment.newInstance()
                Constant.ScreenNumber.Favorite.ordinal -> FavoriteFragment.newInstance()
                else -> HomeFragment.newInstance()
            }
        }

    }


}
