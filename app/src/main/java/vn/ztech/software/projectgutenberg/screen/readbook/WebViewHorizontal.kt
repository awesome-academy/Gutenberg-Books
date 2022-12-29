package vn.ztech.software.projectgutenberg.screen.readbook

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
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
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.getProviderUnzippedBookDirectoryPath
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.extension.concatPath
import vn.ztech.software.projectgutenberg.utils.extension.getPath
import kotlin.math.abs

class WebViewHorizontal(context: Context, attrSet: AttributeSet?) : WebView(context, attrSet) {
    var book: BookLocal? = null
    private val dir
        get() = getProviderUnzippedBookDirectoryPath(context, book?.title ?: "")
    val getPreviousPos
        get() = Math.ceil((--currentPage * this.measuredWidth).toDouble()).toInt()
    val getNextPos
        get() = Math.ceil((++currentPage * this.measuredWidth).toDouble()).toInt()
    var pageCount = 0
    var currentToc = book?.getRecentReadingToc()
    var currentPage = currentToc?.currentReadingPage ?: 0
    var filingThreshold = DEFAULT_FILING_THRESHOLD
    var currentX = 0
    val inflater = LayoutInflater.from(context)
    private var isShowingToolsView = false

    var listener: OnCLickListener? = null

    private val handlerToolsView = Handler(context.mainLooper)
    private val runnableHideToolsVIew = Runnable {
        hideToolsView()
    }

    init {
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
                    webResourceResponse = onRequest(view, url)
                }
                return webResourceResponse
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                this@WebViewHorizontal.evaluateJavascript("javascript:$js") { }
                this@WebViewHorizontal.evaluateJavascript("javascript:alert(initialize())") { }
                currentToc?.let { listener?.onLoadChapterDone(it) }
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
                val pageCount = Integer.parseInt(message)
                this@WebViewHorizontal.pageCount = pageCount
                this@WebViewHorizontal.evaluateJavascript(css) {}
                result.confirm()
                return true
            }
        }
    }

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val deltaX = e2.x - e1.x
            Log.d("MOVEXXX", deltaX.toString())
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
                    moveWithAnimation(previousXPos, deltaX)
                    currentX = previousXPos
                    scrollTo(previousXPos, 0)
                }
            }
            MovePageDirection.NEXT -> {
                if (currentPage == pageCount - 2) {
                    val nextXPos = getNextPos
                    moveWithAnimation(nextXPos, deltaX)
                    currentX = nextXPos
                    scrollTo(nextXPos, 0)
                } else {
                    if (currentPage < pageCount - 1) {
                        val nextXPos = getNextPos
                        moveWithAnimation(nextXPos + PADDING_OFFSET, deltaX)
                        currentX = nextXPos + PADDING_OFFSET
                        scrollTo(nextXPos + PADDING_OFFSET, 0)
                    }
                }
            }
        }
    }

    private fun moveWithAnimation(xPos: Int, deltaX: Float) {
        val animator = ObjectAnimator.ofInt(this, "scrollX", currentX - deltaX.toInt(), xPos)
        animator.duration = SCROLL_DURATION
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    private fun onRequest(view: WebView?, url: String?): WebResourceResponse? {
        var webResourceResponse: WebResourceResponse? = null
        view?.post {
            url?.let { it ->
                webResourceResponse =
                    WebResourceResponse(Constant.EMPTY_STRING, Constant.UTF_8, null)
                val absoluteUri = it.getPath()
                val response = book?.getResourceResponse(absoluteUri)
                response?.let { res ->
                    webResourceResponse!!.data = res.data
                }
            }
        }

        return webResourceResponse
    }

    override fun destroy() {
        super.destroy()
        handlerToolsView.removeCallbacks(runnableHideToolsVIew)
    }

    fun load(tocItem: TocItem? = null) {
        if (tocItem == null) {
            val tocItem = book?.getRecentReadingToc()
            reset()
            tocItem?.let {
                currentToc = it
                this.clearCache(false)
                loadUrl(dir.concatPath(tocItem.href))
            }
        } else {
            this.clearCache(false)
            reset()
            currentToc = tocItem
            loadUrl(dir.concatPath(tocItem.href))
        }
    }

    private fun reset() {
        currentX = 0
        currentPage = 0
    }

    interface OnCLickListener {
        fun updateUISettingView(state: ViewState)
        fun onLoadChapterDone(tocItem: TocItem)
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
        const val js = "function initialize(){\n" +
                "    var d = document.getElementsByTagName('body')[0];\n" +
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
                "    return pageCount;\n" +
                "}"
        const val css = "javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                "style.innerHTML = 'body { padding: 20px 20px !important; }';" +
                "parent.appendChild(style)" +
                "})()"
    }
}
