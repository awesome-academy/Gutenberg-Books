package vn.ztech.software.projectgutenberg.data.model.epub

data class TocItem(
    var bookId: Int = 0,
    var idx: Int = 0,
    var title: String = "",
    var href: String = "",
    var mimeType: String = "",
    var progress: Int = 0,
    var scrollYPosition: Int = 0,
    var isDone: Boolean = false,
    var isSlected: Boolean = false,
    var recentReading: Boolean = false,
    var currentReadingPage: Int = 0
) {
    companion object {
        const val STRING_COVER = "Cover"
    }
}
