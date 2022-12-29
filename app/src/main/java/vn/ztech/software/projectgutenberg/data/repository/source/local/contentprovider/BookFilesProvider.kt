package vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider

import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.BookDbHelper
import vn.ztech.software.projectgutenberg.utils.Constant
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream

/** Currently, the path will only point to public Documents dir
 * , in further version, custom path might be supported*/

fun fetchBooks(context: Context): List<BookLocal> {
    var books: MutableList<BookLocal> = mutableListOf()

    val contentUri = MediaStore.Files.getContentUri(Constant.CONTENT_PROVIDER_BOOK_STORAGE)
    val selection = MediaStore.MediaColumns.DATA + Constant.CONTENT_PROVIDER_BOOK_QUERY_LIKE_COMMAND
    val conditionData =
        getProviderBasePath(context) +
                File.separator +
                Constant.ZIPPED_EBOOK_FOLDER +
                File.separator +
                Constant.CONTENT_PROVIDER_BOOK_QUERY_LIKE_PATTERN
    val conditions = arrayOf(conditionData)
    val cursor = context.contentResolver.query(
        contentUri, null, selection, conditions, null
    )
    if (cursor?.moveToFirst() == true) {
        books = mutableListOf()
        do {
            val titleIdx = cursor.getColumnIndex(BookContentProviderEntry.TITLE)
            val title = cursor.getString(titleIdx)

            val sizeIdx = cursor.getColumnIndex(BookContentProviderEntry.SIZE)
            val size = cursor.getLong(sizeIdx)

            val dataIdx = cursor.getColumnIndex(BookContentProviderEntry.DATA)
            val data = cursor.getString(dataIdx)

            val mimeTypeIdx = cursor.getColumnIndex(BookContentProviderEntry.MIME_TYPE)
            val mimeType = cursor.getString(mimeTypeIdx)

            val displayNameIdx = cursor.getColumnIndex(BookContentProviderEntry.DISPLAY_NAME)
            val displayName = cursor.getString(displayNameIdx)

            val dateModifiedIdx = cursor.getColumnIndex(BookContentProviderEntry.DATE_MODIFIED)
            val dateModified = cursor.getLong(dateModifiedIdx)

            books.add(
                BookLocal(
                    title = title,
                    displayName = displayName,
                    size = size,
                    data = data,
                    dateModified = dateModified,
                    mimeType = mimeType
                )
            )
        } while (cursor.moveToNext())
        cursor.close()

    }
    return books.toList()
}

fun deleteBook(context: Context, book: BookLocal): Boolean {

    val contentUri = MediaStore.Files.getContentUri(Constant.CONTENT_PROVIDER_BOOK_STORAGE)
    val selection =
        MediaStore.MediaColumns.DATA + Constant.CONTENT_PROVIDER_BOOK_QUERY_EQUAL_COMMAND
    val conditions = arrayOf(book.data)
    val numDeleted = context.contentResolver.delete(
        contentUri, selection, conditions
    )
    return numDeleted == BookDbHelper.SQL_EXECUTION_DELETE_ONE_SUCCESS
}

fun getProviderBasePath(context: Context): String {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path +
            File.separator +
            context.resources.getString(R.string.app_name)
}

fun getProviderUnzippedBooksBaseDirectoryPath(context: Context): String {
    return getProviderBasePath(context) +
            File.separator +
            Constant.UNZIPPED_FOLDER_NAME
}

fun getProviderUnzippedBookDirectoryPath(context: Context, title: String): String {
    return getProviderUnzippedBooksBaseDirectoryPath(context) +
            File.separator +
            title
}

fun getInputStreamFromExternalStorage(srcPath: String): InputStream? {
    return BufferedInputStream(File(srcPath).inputStream())
}

object BookContentProviderEntry {
    const val DATE_MODIFIED = MediaStore.MediaColumns.DATE_MODIFIED
    const val DISPLAY_NAME = MediaStore.MediaColumns.DISPLAY_NAME
    const val MIME_TYPE = MediaStore.MediaColumns.MIME_TYPE
    const val DATA = MediaStore.MediaColumns.DATA
    const val SIZE = MediaStore.MediaColumns.SIZE
    const val TITLE = MediaStore.MediaColumns.TITLE
    const val WAIT_TIME_BEFORE_FETCHING_NEWEST_DATA = 100L
}
