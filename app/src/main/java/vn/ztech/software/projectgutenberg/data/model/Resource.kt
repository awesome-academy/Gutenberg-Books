package vn.ztech.software.projectgutenberg.data.model

data class Resource(
    val id: Int,
    val type: String,
    val uri: String
)

object ResourceEntry {
    const val ID = "id"
    const val TYPE = "type"
    const val URI = "uri"
}
