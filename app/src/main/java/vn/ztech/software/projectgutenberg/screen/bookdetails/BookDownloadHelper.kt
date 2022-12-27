package vn.ztech.software.projectgutenberg.screen.bookdetails

import android.app.Activity
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.databinding.FragmentBookdetailsBinding
import vn.ztech.software.projectgutenberg.utils.extension.checkPermissions
import vn.ztech.software.projectgutenberg.utils.extension.download
import vn.ztech.software.projectgutenberg.utils.extension.getAbsolutePathOfFile
import vn.ztech.software.projectgutenberg.utils.extension.isThisFileExist
import vn.ztech.software.projectgutenberg.utils.extension.showAlertDialog
import vn.ztech.software.projectgutenberg.utils.extension.showSnackBar
import java.io.IOException

fun handleDownloadBook(
    activity: Activity?,
    binding: FragmentBookdetailsBinding?,
    data: Pair<Resource?, Book?>,
    handleDownloadResponse: (Int, Int) -> Unit,
    onError: (e: Exception) -> Unit
) {
    activity?.checkPermissions(
        permissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    )
    {
        val selectedDownloadResource = data.first
        val book = data.second
        if (selectedDownloadResource != null && book != null) {
            downloadBook(
                activity,
                binding,
                Pair(selectedDownloadResource, book),
                handleDownloadResponse,
                onError
            )
        }
    }
}

fun downloadBook(
    activity: Activity?,
    binding: FragmentBookdetailsBinding?,
    data: Pair<Resource, Book>,
    handleDownloadResponse: (Int, Int) -> Unit,
    onError: (e: Exception) -> Unit
) {
    if (activity == null) return
    val selectedDownloadResource = data.first
    val book = data.second
    if (!activity.isThisFileExist(activity.getAbsolutePathOfFile(selectedDownloadResource, book))) {
        try {
            activity.download(selectedDownloadResource, book) { status, reason ->
                handleDownloadResponse(status, reason)
            }
            binding?.root?.let {
                activity.showSnackBar(
                    it,
                    String.format(
                        activity.resources.getString(R.string.format_downloading_snackbar),
                        book.title
                    ),
                    activity.resources.getString(R.string.OK)
                ) {}
            }
        } catch (e: IOException) {
            onError(e)
        }

    } else {
        activity.showAlertDialog(
            activity.resources.getString(R.string.dialog_msg_file_existed),
            String.format(activity.resources.getString(R.string.format_file_existed), book.title),
        )
    }
}
