package vn.ztech.software.projectgutenberg.data.model

data class BookLocal(
    val id: Int = DEFAULT_ID,
    val title: String,
    val displayName: String,
    val size: Long,
    val data: String,
    val dateModified: Long,
    val mimeType: String,
    var readingProgress: String = DEFAULT_PROGRESS,
    val imageUrl: String = DEFAULT_IMG_URL
) {
    companion object {
        const val DEFAULT_ID = 0
        const val DEFAULT_PROGRESS = "0"
        const val DEFAULT_IMG_URL = ""
    }
}
