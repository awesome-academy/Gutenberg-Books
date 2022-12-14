package vn.ztech.software.projectgutenberg.utils.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import vn.ztech.software.projectgutenberg.R

fun Activity.checkPermissions(vararg permissions: String, handle: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        if (isAllPermissionGranted(this, permissions = permissions)) {
            handle()
        } else {
            askPermissions(this, permissions = permissions)
        }
    } else {
        handle()
    }
}

private fun askPermissions(activity: Activity, vararg permissions: String) {
    for (permission in permissions) {
        if (!isPermissionGranted(activity, permission)) {
            askPermission(activity, permission)
        }
    }
}

private fun askPermission(activity: Activity, permission: String) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            activity, permission
        )
    ) {
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.dialog_title_permission))
            .setMessage(activity.getString(R.string.dialog_message_permission))
            .setPositiveButton(activity.getString(R.string.dialog_button_allow)) { dialog, id ->
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    permission.hashCode()
                )
            }
            .setNegativeButton(activity.getString(R.string.dialog_button_allow)) { dialog, id -> dialog.cancel() }
            .show()
    } else {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), permission.hashCode())
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
