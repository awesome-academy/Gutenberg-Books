package vn.ztech.software.projectgutenberg.screen.readbook

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.data.repository.source.local.kotpref.KotPref
import vn.ztech.software.projectgutenberg.databinding.ActivityReadBookBinding
import vn.ztech.software.projectgutenberg.di.getReadBookPresenter
import vn.ztech.software.projectgutenberg.screen.download.DownloadFragment
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.base.BaseActivity
import vn.ztech.software.projectgutenberg.utils.extension.getReadingProgressString
import vn.ztech.software.projectgutenberg.utils.extension.getTextSizeFromSeekBarValue
import vn.ztech.software.projectgutenberg.utils.extension.showAlertDialog
import vn.ztech.software.projectgutenberg.utils.extension.toast

class ReadBookActivity : BaseActivity<ActivityReadBookBinding>(ActivityReadBookBinding::inflate),
    TocAdapter.OnClickListener,
    WebViewHorizontal.OnCLickListener {
    var currentBook: BookLocal? = null
    val readBookPresenter by lazy { getReadBookPresenter(this) }
    val tocAdapter = TocAdapter(listener = this)
    lateinit var toggle: ActionBarDrawerToggle

    override fun initView() {
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent?.takeIf { it.hasExtra(BUNDLE_BOOK_LOCAL) }?.let {
            currentBook = it.extras?.getParcelable<BookLocal>(BUNDLE_BOOK_LOCAL)
        }
        currentBook?.let { book ->
            readBookPresenter.getToc(book.id)
            binding?.let {
                setSupportActionBar(it.layoutToolbar.toolbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_reorder_24)

                toggle = ActionBarDrawerToggle(this, it.layoutDrawer, R.string.open, R.string.close)
                toggle.isDrawerIndicatorEnabled = true
                it.layoutDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {
                    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                        //todo leave blank
                    }

                    override fun onDrawerOpened(drawerView: View) {
                        tocAdapter.notifyDataSetChanged()
                    }

                    override fun onDrawerClosed(drawerView: View) {
                        tocAdapter.notifyDataSetChanged()
                    }

                    override fun onDrawerStateChanged(newState: Int) {
                        //todo leave blank
                    }
                })
                it.recyclerToc.adapter = tocAdapter

                it.layoutSettings.seekBarTextSize.setOnSeekBarChangeListener(object :
                    OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        //todo leave blank
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                        //todo leave blank
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        binding?.webViewReadBook?.apply {
                            updateTextSize(seekBar?.progress)
                            latestSettings?.let { settings ->
                                val fontSize = getTextSizeFromSeekBarValue(seekBar?.progress)
                                settings.fontSize = fontSize
                                KotPref.getInstance(applicationContext).save(settings)
                            }
                        }
                    }
                })

                val settings = KotPref.getInstance(applicationContext).getLatestSettings()
                val seekBar = settings?.fromTextSizeToProgressPercentage() ?: 0
                it.layoutSettings.seekBarTextSize.progress = seekBar

                it.layoutSettings.apply {
                    btSave.setOnClickListener { view ->
                        applyAndSaveSettings(
                            it.layoutSettings.layoutColorThemeSetting.
                            chipGroupThemeColor.checkedChipId
                        )
                        binding?.apply {
                            groupToolsView.visibility = View.GONE
                            layoutSettings.viewSetting.startAnimation(
                                AnimationUtils.loadAnimation(
                                    this@ReadBookActivity,
                                    R.anim.anim_top_down
                                )
                            )
                            webViewReadBook.handlerToolsView.removeCallbacksAndMessages(null)
                        }
                    }
                    btCancel.setOnClickListener { view ->
                        binding?.apply {
                            groupToolsView.visibility = View.GONE
                            layoutSettings.viewSetting.startAnimation(
                                AnimationUtils.loadAnimation(
                                    this@ReadBookActivity,
                                    R.anim.anim_top_down
                                )
                            )
                            webViewReadBook.handlerToolsView.removeCallbacksAndMessages(null)
                        }
                    }
                }

            }

        }
        readBookPresenter.setView(mViewObj)
    }

    private fun applyAndSaveSettings(chipId: Int) {
        val pairColor = binding?.webViewReadBook?.latestSettings?.chipIdToThemeColor(chipId)
        pairColor?.let { pair ->
            binding?.webViewReadBook?.apply {
                latestSettings?.textColor = pair.first
                latestSettings?.backgroundColor = pair.second
                latestSettings?.let { KotPref.getInstance(applicationContext).save(it) }
                reloadPage()
            }
            binding?.layoutSettings?.layoutColorThemeSetting?.chipGroupThemeColor?.clearCheck()
        }

    }

    override fun initData() {
        //todo implement later
    }

    val mViewObj = object : ReadBookContract.View {
        override fun onGetTocSuccess(
            data: Toc,
            loadingArea: Constant.LoadingArea,
            action: DownloadFragment.Companion.GetBooksActionType
        ) {
            currentBook?.let { book ->
                binding?.apply {
                    book.epubFile = EpubFile()
                    book.epubFile?.toc = data
                    webViewReadBook.book = currentBook
                    webViewReadBook.load()
                    webViewReadBook.listener = this@ReadBookActivity
                    tocAdapter.setData(book.epubFile?.toc?.listItem ?: emptyList())
                }
            }

        }

        override fun onUpdateReadingProgressDone(tocItem: TocItem) {
            tocAdapter.updateTocItem(tocItem)
        }

        override fun onError(e: Exception?) {
            toast(e?.message.toString())
        }

        override fun updateLoading(
            loadingArea: Constant.LoadingArea,
            state: Constant.LoadingState
        ) {

            val visibility = if (state == Constant.LoadingState.SHOW) View.VISIBLE else View.GONE

            if (loadingArea is Constant.LoadingAreaReadBook) {
                when (loadingArea) {
                    Constant.LoadingAreaReadBook.ReadBookMain -> {
                        binding?.layoutLoading?.layoutLoading?.visibility = visibility
                    }
                }
            }
        }

    }

    override fun onTocItemClick(tocItem: TocItem) {
        tocAdapter.currentSelectedItem = tocItem
        binding?.let {
            it.webViewReadBook?.load(tocItem)
            it.layoutDrawer.closeDrawer(GravityCompat.START)
        }
    }

    override fun updateUISettingView(state: WebViewHorizontal.Companion.ViewState) {
        when (state) {
            WebViewHorizontal.Companion.ViewState.HIDE -> {
                binding?.groupToolsView?.visibility = View.GONE
                binding?.layoutSettings?.viewSetting?.startAnimation(
                    AnimationUtils.loadAnimation(this@ReadBookActivity, R.anim.anim_top_down)
                )

            }
            WebViewHorizontal.Companion.ViewState.SHOW -> {
                if (binding?.groupToolsView?.visibility != View.VISIBLE) {
                    binding?.groupToolsView?.visibility = View.VISIBLE
                    binding?.layoutSettings?.viewSetting?.startAnimation(
                        AnimationUtils.loadAnimation(
                            this@ReadBookActivity, R.anim.anim_bottom_up
                        )
                    )
                }
            }
        }
    }

    override fun onLoadChapterDone(tocItem: TocItem) {
        tocAdapter.updateCurrentSelectedItem(tocItem)
        readBookPresenter.updateLatestReadingTocItem(tocItem)
        binding?.layoutToolbar?.toolbar?.title = tocItem.title
    }

    override fun updateReadingProgress(tocItem: TocItem, currentPage: Int, totalPage: Int) {
        readBookPresenter.updateReadingProgress(
            tocItem,
            getReadingProgressString(currentPage, totalPage)
        )
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding != null) {
            if (binding!!.layoutDrawer.isDrawerOpen(GravityCompat.START)) {
                binding!!.layoutDrawer.closeDrawer(GravityCompat.START)
            } else {
                this.showAlertDialog(
                    R.string.dialog_title_exit_reading,
                    R.string.dialog_message_exit_reading,
                    onClickOkListener = { _, _ -> finish() }
                )
            }
        } else {
            this.showAlertDialog(
                R.string.dialog_title_exit_reading,
                R.string.dialog_message_exit_reading,
                onClickOkListener = { _, _ -> finish() }
            )
        }
    }

    companion object {
        const val BUNDLE_BOOK_LOCAL = "BUNDLE_BOOK_LOCAL"
    }
}
