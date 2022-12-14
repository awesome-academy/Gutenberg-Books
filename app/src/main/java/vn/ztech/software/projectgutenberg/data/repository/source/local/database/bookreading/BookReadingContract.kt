package vn.ztech.software.projectgutenberg.data.repository.source.local.database.bookreading

import android.provider.BaseColumns

/**Book reading table will store the table of contents of books, and it also store the reading progress of users.*/
object BookReadingContract {
    object BookReadingEntry : BaseColumns {
        const val TABLE_NAME = "book_reading"
        const val COLUMN_NAME_TOC_IDX = "idx"
        const val COLUMN_NAME_TOC_ITEM_TITLE = "toc_title"
        const val COLUMN_NAME_TOC_ITEM_HREF = "toc_href"
        const val COLUMN_NAME_TOC_ITEM_MIME_TYPE = "mime_type"
        const val COLUMN_NAME_PROGRESS = "progress"
        const val COLUMN_NAME_IS_LATEST_READING = "is_latest_reading"
    }
}
