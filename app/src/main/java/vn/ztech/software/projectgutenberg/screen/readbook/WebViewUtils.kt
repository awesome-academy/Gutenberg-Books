package vn.ztech.software.projectgutenberg.screen.readbook

import android.animation.ObjectAnimator
import android.view.animation.LinearInterpolator
import android.webkit.WebResourceResponse
import android.webkit.WebView
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.epub.Settings
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.extension.getPath

fun moveWithAnimation(webView: WebView, currentX: Int, xPos: Int, deltaX: Float) {
    val animator = ObjectAnimator.ofInt(
        webView, "scrollX", currentX - deltaX.toInt(), xPos
    )
    animator.duration = WebViewHorizontal.SCROLL_DURATION
    animator.interpolator = LinearInterpolator()
    animator.start()
}

fun onRequest(book: BookLocal?, view: WebView?, url: String?): WebResourceResponse? {
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

fun getJs(latestSettings: Settings?): String {
    if (latestSettings == null)
        return WebViewHorizontal.defaultJs
    val x = concatJS(latestSettings.getScriptFontSize(latestSettings.fontSize))
    return x
}

fun concatJS(settingsJS: String): String {
    return WebViewHorizontal.BASE_FUNCTION_SETTINGS_START_STRING +
            settingsJS + WebViewHorizontal.JS_PAGINATE
}

fun concatCSS(settingsCSS: String): String {
    return WebViewHorizontal.BASE_FUNCTION_CSS_START_STRING +
            settingsCSS + WebViewHorizontal.BASE_FUNCTION_CSS_END_STRING
}
