package vn.ztech.software.projectgutenberg.utils.extension

import android.content.Context
import android.net.Uri
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.getProviderBasePath
import vn.ztech.software.projectgutenberg.utils.Constant
import java.io.File
import java.io.IOException

fun getUnzipAbsolutePath(context: Context, bookTitle: String): String {
    return getProviderBasePath(context) +
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
