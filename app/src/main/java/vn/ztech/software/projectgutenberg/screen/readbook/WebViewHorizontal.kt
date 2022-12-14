package vn.ztech.software.projectgutenberg.screen.readbook

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.epub.Settings
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.getProviderUnzippedBookDirectoryPath
import vn.ztech.software.projectgutenberg.data.repository.source.local.kotpref.KotPref
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.extension.concatPath
import vn.ztech.software.projectgutenberg.utils.extension.getPath
import vn.ztech.software.projectgutenberg.utils.extension.getTextSizeFromSeekBarValue
import kotlin.math.abs

class WebViewHorizontal(context: Context, attrSet: AttributeSet?) : WebView(context, attrSet) {
    var book: BookLocal? = null
    private val dir
        get() = getProviderUnzippedBookDirectoryPath(book?.title ?: "")
    val getPreviousPos
        get() = Math.ceil((--currentPage * this.measuredWidth).toDouble()).toInt()
    val getNextPos
        get() = Math.ceil((++currentPage * this.measuredWidth).toDouble()).toInt()
    var pageCount = 0
    var currentToc = book?.getRecentReadingToc()
    var currentPage = 0
    var filingThreshold = DEFAULT_FILING_THRESHOLD
    var currentX = 0
    val inflater = LayoutInflater.from(context)
    private var isShowingToolsView = false
    var latestSettings: Settings? = null

    var listener: OnCLickListener? = null

    val handlerToolsView = Handler(context.mainLooper)
    private val runnableHideToolsVIew = Runnable {
        hideToolsView()
    }

    init {
        latestSettings = KotPref.getInstance(context).getLatestSettings() ?: Settings()

        /**Calculate filing threshold*/
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        filingThreshold = (displayMetrics.widthPixels * HORIZONTAL_FILING_DELTA_PERCENTAGE).toInt()

        /**Config webview*/
        this.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                var webResourceResponse: WebResourceResponse? = null
                view?.post {
                    webResourceResponse = onRequest(book, view, url)
                }
                return webResourceResponse
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                this@WebViewHorizontal.evaluateJavascript("javascript:alert(${getJs(latestSettings)}())") {
                    currentPage?.let {
                        if (it > 0) {
                            --currentPage
                            movePage(0f, MovePageDirection.NEXT)
                        }
                    }
                }

                /**Incase of user clicking in the link inside an html file,
                 * find the toc that contain that link,
                 * then update UI to synchronize the drawer with the have-just-clicked link*/
                url?.let {
                    val justLoadedToc = book?.getTocByWebViewUrl(url)
                    if (justLoadedToc != null) currentToc = justLoadedToc
                }
                currentToc?.let {
                    val pageHistory = currentToc?.getCurrentReadingPage() ?: Pair(0, 0)
                    currentPage = pageHistory.first
                    pageCount = pageHistory.second
                    listener?.onLoadChapterDone(it)
                }
            }
        }
        val settings = settings
        settings.apply {
            cacheMode = WebSettings.LOAD_NO_CACHE
            builtInZoomControls = false
            allowFileAccess = true
            allowContentAccess = true
            displayZoomControls = false
            javaScriptEnabled = true
        }
        this.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult
            ): Boolean {
                if (message.isNullOrEmpty())
                    return true
                val pageCount = Integer.parseInt(message?.split(" ")?.first())
                currentPage = getNewPageFromNewPageCount(
                    currentPage,
                    this@WebViewHorizontal.pageCount,
                    pageCount
                )
                this@WebViewHorizontal.pageCount = pageCount
                currentToc?.let { listener?.updateReadingProgress(it, currentPage, pageCount) }
                this@WebViewHorizontal.evaluateJavascript(
                    concatCSS(
                        latestSettings?.getScriptCSS() ?: ""
                    )
                ) {}
                result.confirm()
                return true
            }
        }
    }

    private fun getNewPageFromNewPageCount(
        currentPage: Int,
        oldPageCount: Int,
        newPageCount: Int
    ): Int {
        if (oldPageCount == 0) return 0
        return currentPage * newPageCount / oldPageCount
    }

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val deltaX = e2.x - e1.x
            if (abs(deltaX) > filingThreshold) {
                return if (e2.x > e1.x) {
                    /**Swipe right, that means we need to move to the previous page*/
                    movePage(deltaX, MovePageDirection.PREVIOUS)
                    true
                } else {
                    /**Swipe left, that means we need to move to the next page*/
                    movePage(deltaX, MovePageDirection.NEXT)
                    true
                }
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            showToolsView()
            return true
        }

    }
    val gestureDetector = GestureDetector(this.context, gestureListener)

    private fun showToolsView() {
        listener?.updateUISettingView(ViewState.SHOW)
        isShowingToolsView = true
        handlerToolsView.removeCallbacksAndMessages(null)
        handlerToolsView.postDelayed({ runnableHideToolsVIew.run() }, TOOLS_VIEW_DELAY_TIME)
    }

    private fun hideToolsView() {
        isShowingToolsView = false
        listener?.updateUISettingView(ViewState.HIDE)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun movePage(deltaX: Float, direction: MovePageDirection) {
        when (direction) {
            MovePageDirection.PREVIOUS -> {
                if (currentPage > 0) {
                    val previousXPos = getPreviousPos
                    moveWithAnimation(this@WebViewHorizontal, currentX, previousXPos, deltaX)
                    currentX = previousXPos
                    scrollTo(previousXPos, 0)
                    currentToc?.let {
                        listener?.updateReadingProgress(it, currentPage, pageCount)
                    }
                }
            }
            MovePageDirection.NEXT -> {
                if (currentPage == pageCount - 2) {
                    val nextXPos = getNextPos
                    moveWithAnimation(this@WebViewHorizontal, currentX, nextXPos, deltaX)
                    currentX = nextXPos
                    scrollTo(nextXPos, 0)
                    currentToc?.let {
                        listener?.updateReadingProgress(it, currentPage, pageCount)
                    }
                    return
                }

                if (currentPage < pageCount - 1) {
                    val nextXPos = getNextPos
                    moveWithAnimation(this@WebViewHorizontal, currentX, nextXPos + PADDING_OFFSET, deltaX)
                    currentX = nextXPos + PADDING_OFFSET
                    scrollTo(nextXPos + PADDING_OFFSET, 0)
                    currentToc?.let {
                        listener?.updateReadingProgress(it, currentPage, pageCount)
                    }
                }
            }
        }
    }

    override fun destroy() {
        super.destroy()
        handlerToolsView.removeCallbacksAndMessages(null)
    }

    fun load(tocItem: TocItem? = null) {
        if (tocItem == null) {
            val tocItem = book?.getRecentReadingToc()
            reset()
            tocItem?.let {
                currentToc = it
                val pageHistory = currentToc?.getCurrentReadingPage() ?: Pair(0, 0)
                currentPage = pageHistory.first
                pageCount = pageHistory.second
                this.clearCache(false)
                loadUrl(dir.concatPath(tocItem.href))
            }
        } else {
            this.clearCache(false)
            reset()
            currentToc = tocItem
            val pageHistory = currentToc?.getCurrentReadingPage() ?: Pair(0, 0)
            currentPage = pageHistory.first
            pageCount = pageHistory.second
            loadUrl(dir.concatPath(tocItem.href))
        }
    }

    fun reloadPage() {
        load(currentToc)
    }

    private fun reset() {
        currentX = 0
        currentPage = 0
    }


    fun updateTextSize(seekBarProgress: Int?) {
        seekBarProgress?.let {
            val textSize = getTextSizeFromSeekBarValue(seekBarProgress)
            latestSettings?.fontSize = textSize
            load(currentToc)
        }
    }



    interface OnCLickListener {
        fun updateUISettingView(state: ViewState)
        fun onLoadChapterDone(tocItem: TocItem)
        fun updateReadingProgress(tocItem: TocItem, currentPage: Int, totalPage: Int)
    }

    companion object {
        const val HORIZONTAL_FILING_DELTA_PERCENTAGE = 0.04
        const val SCROLL_DURATION: Long = 400
        const val PADDING_OFFSET = 0

        enum class MovePageDirection {
            PREVIOUS,
            NEXT
        }

        enum class ViewState {
            SHOW,
            HIDE
        }

        val TAG = WebViewHorizontal::class.java.name.toString()
        const val TOOLS_VIEW_DELAY_TIME: Long = 4000
        const val DEFAULT_FILING_THRESHOLD = 30
        const val BASE_FUNCTION_SETTINGS_START_STRING = "function settings() {\n" +
                "var d = document.getElementsByTagName('body')[0];\n"

        const val JS_PAGINATE =
            "    var ourH = window.innerHeight - 40;\n" +
                    "    var ourW = window.innerWidth - (2*20);\n" +
                    "    var fullH = d.offsetHeight;\n" +
                    "    var pageCount = Math.floor(fullH/ourH)+1;\n" +
                    "    var currentPage = 0;\n" +
                    "    var newW = pageCount*window.innerWidth - (2*20);\n" +
                    "    d.style.height = ourH+'px';\n" +
                    "    d.style.width = newW+'px';\n" +
                    "    d.style.margin = 0;\n" +
                    "    d.style.webkitColumnGap = '40px';\n" +
                    "    d.style.webkitColumnCount = pageCount;\n" +
                    "    document.head.innerHTML = document.head.innerHTML " +
                    "+ '<meta name=\"viewport\" content=\"height=device-height, user-scalable=no\" />';" +
                    "    return pageCount + ' ' + fullH + ' ' + ourH + ' ' + ourW;\n" +
                    "}"
        const val defaultJs = BASE_FUNCTION_SETTINGS_START_STRING + JS_PAGINATE

        const val BASE_FUNCTION_CSS_START_STRING = "javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                "style.innerHTML = 'body { padding: 20px 20px !important;"
        const val BASE_FUNCTION_CSS_END_STRING = "';" +
                "parent.appendChild(style)" +
                "})()"
        const val DEFAULT_CSS = BASE_FUNCTION_CSS_START_STRING + BASE_FUNCTION_CSS_END_STRING
        const val DEFAULT_MIN_TEXT_SIZE = 10
        const val DEFAULT_MAX_TEXT_SIZE = 19
        const val DEFAULT_TEXT_SIZE = 15
    }

}
