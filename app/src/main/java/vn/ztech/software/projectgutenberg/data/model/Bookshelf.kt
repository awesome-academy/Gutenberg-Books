package vn.ztech.software.projectgutenberg.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bookshelf(
    val id: Int,
    val name: String
) : Parcelable

object BookshelfEntry {
    const val ID = "id"
    const val NAME = "name"
}
