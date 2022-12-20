package vn.ztech.software.projectgutenberg.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Resource(
    val id: Int,
    val type: String,
    val uri: String
) : Parcelable

object ResourceEntry {
    const val ID = "id"
    const val TYPE = "type"
    const val URI = "uri"
}
