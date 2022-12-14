package vn.ztech.software.projectgutenberg.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Agent(
    val id: Int,
    val person: String,
    val type: String
) : Parcelable

object AgentEntry {
    const val ID = "id"
    const val PERSON = "person"
    const val TYPE = "type"
    const val TYPE_AUTHOR = "Author"

}
