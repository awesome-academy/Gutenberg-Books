package vn.ztech.software.projectgutenberg.utils.extension

import android.content.Context
import android.net.Uri
import vn.ztech.software.projectgutenberg.data.model.epub.Settings
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.getProviderBasePath
import vn.ztech.software.projectgutenberg.screen.readbook.WebViewHorizontal
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.Constant.ONE_HUNDRED_PERCENT
import java.io.File
import java.io.IOException

fun getUnzipAbsolutePath(bookTitle: String): String {
    return getProviderBasePath() +
            File.separator +
            Constant.UNZIPPED_FOLDER_NAME +
            File.separator +
            bookTitle
}

fun String.getPathExceptFileName(): String {
    return this.substring(0, this.lastIndexOf(File.separator))
}

fun String.concatPath(relativePath: String): String {
    val raw = if (this.isEmpty()/* || relativePath.startsWith(File.separator)*/) {
        relativePath
    } else {
        "${this}${File.separator}${relativePath}"
    }
    try {
        return File(raw).canonicalPath.toString()
    } finally {
        //todo handle later
    }
}

fun getReadingProgressString(currentPage: Int, totalPage: Int): String {
    return "${currentPage}/${totalPage}"
}

fun String.getParentPath(): String {
    return try {
        val path: String? = File(this).canonicalFile.parent
        // remove leading '/'
        path?.substring(1) ?: ""
    } catch (e: IOException) {
        throw java.lang.RuntimeException(e)
    }
}

fun String.getPath(): String {
    val uri = Uri.parse(this)
    return uri.path ?: ""
}

fun getTextSizeFromSeekBarValue(seekBarValue: Int?): Int {
    if (seekBarValue == null) return Settings.DEFAULT_VALUE_NOT_EXIST
    return WebViewHorizontal.DEFAULT_MIN_TEXT_SIZE +
            ((WebViewHorizontal.DEFAULT_MAX_TEXT_SIZE -
                    WebViewHorizontal.DEFAULT_MIN_TEXT_SIZE) *
                    seekBarValue) / ONE_HUNDRED_PERCENT
}
