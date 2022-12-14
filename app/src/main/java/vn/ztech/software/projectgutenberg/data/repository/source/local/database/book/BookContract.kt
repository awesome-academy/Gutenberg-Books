package vn.ztech.software.projectgutenberg.data.repository.source.local.database.book

import android.provider.BaseColumns

object BookContract {

    object BookEntry : BaseColumns {
        const val TABLE_NAME = "book"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_DATA = "data"
        const val COLUMN_NAME_SIZE = "size"
        const val COLUMN_NAME_MIME_TYPE = "mime_type"
        const val COLUMN_NAME_READING_PROGRESS = "reading_progress"
        const val COLUMN_NAME_DATE_MODIFIED = "date_modified"
        const val COLUMN_NAME_DISPLAY_NAME = "display_name"
        const val COLUMN_NAME_IMAGE_URL = "img_url"
        const val COLUMN_NAME_PREPARED = "prepared"
    }

    enum class SearchableBookEntry(val value: String) {
        COLUMN_NAME_TITLE(BookEntry.COLUMN_NAME_TITLE),
        COLUMN_NAME_MIME_TYPE(BookEntry.COLUMN_NAME_MIME_TYPE)
    }
}
