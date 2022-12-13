package vn.ztech.software.projectgutenberg.utils.extension

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.annotation.StringRes
import vn.ztech.software.projectgutenberg.R

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showAlertDialog(
    @StringRes titleStringId: Int,
    @StringRes messageStringId: Int,
    onClickCancelListener: DialogInterface.OnClickListener,
    onClickOkListener: DialogInterface.OnClickListener
) {
    AlertDialog.Builder(this)
        .setTitle(this.resources.getString(titleStringId))
        .setMessage(this.resources.getString(messageStringId))
        .setNegativeButton(R.string.dialog_button_cancel, onClickCancelListener)
        .setPositiveButton(R.string.dialog_button_ok, onClickOkListener)
        .show()
}
