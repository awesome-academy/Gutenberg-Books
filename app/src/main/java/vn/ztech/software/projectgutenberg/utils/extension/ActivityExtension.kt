package vn.ztech.software.projectgutenberg.utils.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import vn.ztech.software.projectgutenberg.R
import kotlin.math.absoluteValue

fun Activity.checkPermissions(vararg permissions: String, handle: () -> Unit) {
    if (isAllPermissionGranted(this, permissions = permissions)) {
        handle()
    } else {
        askPermissions(this, permissions = permissions)
    }
}

private fun askPermissions(activity: Activity, vararg permissions: String) {
    val listPermissions = mutableListOf<String>()

    for (permission in permissions) {
        if (!isPermissionGranted(activity, permission)) {
            listPermissions.add(permission)
        }
    }

    askPermission(activity, listPermissions)

}

private fun askPermission(activity: Activity, permissions: MutableList<String>) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            activity, permissions[0]
        )
    ) {
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.dialog_title_permission))
            .setMessage(activity.getString(R.string.dialog_message_permission))
            .setPositiveButton(activity.getString(R.string.dialog_button_allow)) { dialog, id ->
                ActivityCompat.requestPermissions(
                    activity,
                    permissions.toTypedArray(),
                    permissions.hashCode().absoluteValue
                )
            }
            .setNegativeButton(activity.getString(R.string.dialog_button_allow)) { dialog, id -> dialog.cancel() }
            .show()
    } else {
        ActivityCompat.requestPermissions(
            activity,
            permissions.toTypedArray(),
            permissions.hashCode().absoluteValue
        )
    }
}

private fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

private fun isAllPermissionGranted(context: Context, vararg permissions: String): Boolean =
    permissions.all { permission ->
        ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
