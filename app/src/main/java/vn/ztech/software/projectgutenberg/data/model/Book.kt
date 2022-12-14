package vn.ztech.software.projectgutenberg.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Book(
    val agents: List<Agent>,
    val bookshelves: List<String>,
    val description: String,
    val downloads: Int,
    val id: Int,
    val languages: List<String>,
    val license: String,
    val resources: List<Resource>,
    val subjects: List<String>,
    val title: String,
) : Parcelable

object BookEntry {
    const val AGENTS = "agents"
    const val BOOKSHELVES = "bookshelves"
    const val DESCRIPTION = "description"
    const val DOWNLOADS = "downloads"
    const val ID = "id"
    const val LANGUAGES = "languages"
    const val LICENSE = "license"
    const val RESOURCES = "resources"
    const val SUBJECTS = "subjects"
    const val TITLE = "title"
}
