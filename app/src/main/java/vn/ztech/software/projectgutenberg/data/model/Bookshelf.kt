package vn.ztech.software.projectgutenberg.data.model

data class Bookshelf(
    val id: Int,
    val name: String
)

object BookshelfEntry {
    const val ID = "id"
    const val NAME = "name"
}
