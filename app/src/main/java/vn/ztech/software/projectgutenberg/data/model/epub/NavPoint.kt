package vn.ztech.software.projectgutenberg.data.model.epub

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NavPoint(
    val playOrder: Int,
    var navLabel: String = DEFAULT_VALUE,
    var content: String = DEFAULT_VALUE
) : Parcelable {

    constructor(playOrder: String) : this(playOrder.toInt(), DEFAULT_VALUE, DEFAULT_VALUE)

    companion object {
        const val DEFAULT_VALUE = ""
    }
}
