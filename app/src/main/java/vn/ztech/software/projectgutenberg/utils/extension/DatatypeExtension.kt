package vn.ztech.software.projectgutenberg.utils.extension

import android.app.DownloadManager
import vn.ztech.software.projectgutenberg.utils.Constant

fun String.formatUriPath(): String {
    return this.replace(Regex(Constant.URI_FORMAT_REGEX), Constant.URI_FORMAT_REPLACEMENT)
}

fun Int.statusToMsg(): String {
    return when (this) {
        DownloadManager.STATUS_FAILED -> "Download failed"
        DownloadManager.STATUS_PAUSED -> "Download paused"
        DownloadManager.STATUS_PENDING -> "Download is pending"
        DownloadManager.STATUS_RUNNING -> "Downloading..."
        DownloadManager.STATUS_SUCCESSFUL -> "Download successfully"
        else -> {
            ""
        }
    }
}

fun Int.reasonToMsg(): String {
    return when (this) {
        /** Those messages should be changed to adapt the UX of this app*/
        DownloadManager.ERROR_CANNOT_RESUME -> "Can not resume"
        DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Device not found"
        DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "File already exists"
        DownloadManager.ERROR_FILE_ERROR -> "File error"
        DownloadManager.ERROR_HTTP_DATA_ERROR -> "Http data error"
        DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Insufficient space"
        DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirect"
        DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "Unhandled http code"
        DownloadManager.ERROR_UNKNOWN -> "Unknown error"
        DownloadManager.PAUSED_QUEUED_FOR_WIFI -> "Waiting for wifi"
        DownloadManager.PAUSED_UNKNOWN -> "Paused unknown"
        DownloadManager.PAUSED_WAITING_FOR_NETWORK -> "Waiting for network"
        else -> ""
    }
}

val listStatusShouldShow = listOf<Int>(
    DownloadManager.STATUS_PAUSED,
    DownloadManager.STATUS_FAILED,
    DownloadManager.STATUS_SUCCESSFUL
)
val listReasonShouldShow = listOf(
    DownloadManager.ERROR_CANNOT_RESUME,
    DownloadManager.ERROR_DEVICE_NOT_FOUND,
    DownloadManager.ERROR_FILE_ALREADY_EXISTS,
    DownloadManager.ERROR_FILE_ERROR,
    DownloadManager.ERROR_HTTP_DATA_ERROR,
    DownloadManager.ERROR_INSUFFICIENT_SPACE,
    DownloadManager.ERROR_TOO_MANY_REDIRECTS,
    DownloadManager.ERROR_UNHANDLED_HTTP_CODE,
    DownloadManager.ERROR_UNKNOWN,
    DownloadManager.PAUSED_QUEUED_FOR_WIFI,
    DownloadManager.PAUSED_UNKNOWN,
    DownloadManager.PAUSED_WAITING_FOR_NETWORK,
    DownloadManager.PAUSED_WAITING_TO_RETRY,
)
