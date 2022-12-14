package vn.ztech.software.projectgutenberg.utils.extension

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.BookFilesProvider.Companion.BookContentProviderEntry
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.BookFilesProvider
import vn.ztech.software.projectgutenberg.di.getBookLocalReadableDataSource
import vn.ztech.software.projectgutenberg.di.getBookLocalWritableDataSource
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.Constant.DOWNLOAD_UPDATE_PROGRESS_SLEEP_TIME
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showAlertDialog(
    @StringRes titleStringId: Int,
    @StringRes messageStringId: Int,
    onClickCancelListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> },
    onClickOkListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> }
) {
    AlertDialog.Builder(this)
        .setTitle(this.resources.getString(titleStringId))
        .setMessage(this.resources.getString(messageStringId))
        .setNegativeButton(R.string.dialog_button_cancel, onClickCancelListener)
        .setPositiveButton(R.string.dialog_button_ok, onClickOkListener)
        .show()
}

fun Context.showAlertDialog(
    titleStringId: String,
    messageStringId: String,
    onClickCancelListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> },
    onClickOkListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { _, _ -> }
) {
    AlertDialog.Builder(this)
        .setTitle(titleStringId)
        .setMessage(messageStringId)
        .setNegativeButton(R.string.dialog_button_cancel, onClickCancelListener)
        .setPositiveButton(R.string.dialog_button_ok, onClickOkListener)
        .show()
}

fun Context.showSnackBar(view: View, msg: String, actionMsg: String, action: () -> Unit): Snackbar {
    val snackBar = Snackbar.make(view, msg, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionMsg) {
            action()
        }
    snackBar.show()
    return snackBar
}

@SuppressLint("Range")
fun Context.download(
    resource: Resource,
    mbook: Book?,
    callback: (status: Int, reason: Int) -> Unit
) {
    mbook?.let { book ->
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(resource.uri)
        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
            )
                .setAllowedOverRoaming(true)
                .setTitle(
                    String.format(
                        resources.getString(
                            R.string.format_donwload_noti_title,
                            book.title
                        )
                    )
                )
                .setDescription(
                    String.format(
                        resources.getString(
                            R.string.format_donwload_noti_description,
                            book.description
                        )
                    )
                )
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOCUMENTS,
                    getRelativePathOfFile(resource, book)
                )
        }
        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            Thread.sleep(DOWNLOAD_UPDATE_PROGRESS_SLEEP_TIME)

            var downloading = true
            while (downloading) {

                val cursor = downloadManager.query(query)
                if (cursor == null || !cursor.moveToFirst()) return@execute

                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                    Thread.sleep(BookContentProviderEntry.WAIT_TIME_BEFORE_FETCHING_NEWEST_DATA)
                    getBookLocalReadableDataSource(this).let { dataSource ->
                        dataSource.scanLocalStorage(
                            this@download,
                            object : OnResultListener<Boolean> {
                                override fun onSuccess(data: Boolean) {
                                    getBookLocalWritableDataSource(this@download).updateBookImageUrl(
                                        resource,
                                        this@download.getFileTitle(resource, book),
                                        book.resources.findCoverImageURL() ?: ""
                                    )
                                }

                                override fun onError(e: Exception?) { /* todo handle this later */
                                }

                            })
                    }
                }

                val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))

                handler.post { callback(status, reason) }
            }
        }
        executor.shutdown()
    }
}

fun Context.getAbsolutePathOfFile(resource: Resource, book: Book): String {
    return "" +
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)}" +
            "${File.separator}" +
            getRelativePathOfFile(resource, book)
}

fun Context.getRelativePathOfFile(resource: Resource, book: Book): String {
    val result = "" +
            "${this.resources.getString(R.string.app_name)}" +
            File.separator +
            Constant.ZIPPED_EBOOK_FOLDER +
            File.separator +
            book.title +
            "${resource.uri.substring(resource.uri.lastIndexOf("/") + 1)}." +
            resource.kindShort
    return result.formatUriPath()
}





