package vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider

import android.content.ContentResolver
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.BookDbHelper
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookDbDAOImpl
import vn.ztech.software.projectgutenberg.utils.Constant
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream

/** Currently, the path will only point to public Documents dir
 * , in further version, custom path might be supported*/

class BookFilesProvider{
    fun fetchBooks(): List<BookLocal> {
        var books: MutableList<BookLocal> = mutableListOf()

        val contentUri = MediaStore.Files.getContentUri(Constant.CONTENT_PROVIDER_BOOK_STORAGE)
        val selection = MediaStore.MediaColumns.DATA + Constant.CONTENT_PROVIDER_BOOK_QUERY_LIKE_COMMAND
        val conditionData =
            getProviderBasePath() +
                    File.separator +
                    Constant.ZIPPED_EBOOK_FOLDER +
                    File.separator +
                    Constant.CONTENT_PROVIDER_BOOK_QUERY_LIKE_PATTERN
        val conditions = arrayOf(conditionData)
        val cursor = contentResolver?.query(
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

    fun deleteBook(book: BookLocal): Boolean {

        val contentUri = MediaStore.Files.getContentUri(Constant.CONTENT_PROVIDER_BOOK_STORAGE)
        val selection =
            MediaStore.MediaColumns.DATA + Constant.CONTENT_PROVIDER_BOOK_QUERY_EQUAL_COMMAND
        val conditions = arrayOf(book.data)
        val numDeleted = contentResolver?.delete(
            contentUri, selection, conditions
        )
        return numDeleted == BookDbHelper.SQL_EXECUTION_DELETE_ONE_SUCCESS
    }




    companion object {
        object BookContentProviderEntry {
            const val DATE_MODIFIED = MediaStore.MediaColumns.DATE_MODIFIED
            const val DISPLAY_NAME = MediaStore.MediaColumns.DISPLAY_NAME
            const val MIME_TYPE = MediaStore.MediaColumns.MIME_TYPE
            const val DATA = MediaStore.MediaColumns.DATA
            const val SIZE = MediaStore.MediaColumns.SIZE
            const val TITLE = MediaStore.MediaColumns.TITLE
            const val WAIT_TIME_BEFORE_FETCHING_NEWEST_DATA = 100L
        }
        const val APP_NAME = "ProjectGutenberg"

        private var instance: BookFilesProvider? = null
        private var contentResolver: ContentResolver? = null
        fun getInstance(context: Context?) = synchronized(this) {
            if (contentResolver == null){
                context?.let { contentResolver = context.contentResolver }
            }
            instance ?: BookFilesProvider().also { instance = it }
        }
    }
}



