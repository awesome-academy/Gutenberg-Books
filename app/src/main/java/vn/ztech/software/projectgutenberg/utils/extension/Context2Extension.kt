package vn.ztech.software.projectgutenberg.utils.extension

import android.content.ContentValues
import android.content.Context
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookContract
import java.io.File

fun Context.getFileTitle(resource: Resource, book: Book): String {
    val result = "" +
            "${book.title}" +
            "${resource.uri.substring(resource.uri.lastIndexOf("/") + 1)}"
    return result.formatUriPath()
}

fun Context.isThisFileExist(path: String): Boolean {
    val file = File(path)
    return file.exists()
}

fun BookLocal.toContentValues(): ContentValues {

    return ContentValues().apply {
        put(BookContract.BookEntry.COLUMN_NAME_TITLE, this@toContentValues.title)
        put(BookContract.BookEntry.COLUMN_NAME_DATA, this@toContentValues.data)
        put(BookContract.BookEntry.COLUMN_NAME_SIZE, this@toContentValues.size)
        put(BookContract.BookEntry.COLUMN_NAME_MIME_TYPE, this@toContentValues.mimeType)
        put(
            BookContract.BookEntry.COLUMN_NAME_READING_PROGRESS,
            this@toContentValues.readingProgress
        )
        put(BookContract.BookEntry.COLUMN_NAME_DATE_MODIFIED, this@toContentValues.dateModified)
        put(BookContract.BookEntry.COLUMN_NAME_DISPLAY_NAME, this@toContentValues.displayName)
        put(BookContract.BookEntry.COLUMN_NAME_PREPARED, this@toContentValues.prepared)

    }
}
