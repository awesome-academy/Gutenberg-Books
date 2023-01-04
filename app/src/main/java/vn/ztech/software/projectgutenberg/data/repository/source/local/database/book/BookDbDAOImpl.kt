package vn.ztech.software.projectgutenberg.data.repository.source.local.database.book

import android.content.ContentValues
import android.database.Cursor
import android.provider.BaseColumns
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.BookDbHelper
import vn.ztech.software.projectgutenberg.utils.Constant
import vn.ztech.software.projectgutenberg.utils.extension.toContentValues
import vn.ztech.software.projectgutenberg.utils.extension.toImgUrlContentValue

class BookDbDAOImpl private constructor(dbHelper: BookDbHelper) {
    private val writableDb by lazy { dbHelper.writableDatabase }
    private val readableDb by lazy { dbHelper.readableDatabase }
    val readableDAOObj = object : BookDbDAOReadableInterface {
        override fun getBooks(offset: Int): List<BookLocal> {
            val cursor = readableDb.query(
                BookContract.BookEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                "${offset}, ${Constant.LOCAL_DATA_QUERY_PAGE_SIZE}"
            )
            if (!cursor.moveToFirst()) return emptyList()
            val books = mutableListOf<BookLocal>()
            do {
                val idIdx = cursor.getColumnIndex(BaseColumns._ID)
                val id = cursor.getInt(idIdx)

                val titleIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_TITLE)
                val title = cursor.getString(titleIdx)

                val sizeIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_SIZE)
                val size = cursor.getLong(sizeIdx)

                val dataIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DATA)
                val data = cursor.getString(dataIdx)

                val mimeTypeIdx =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_MIME_TYPE)
                val mimeType = cursor.getString(mimeTypeIdx)

                val dateModifiedIdx =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DATE_MODIFIED)
                val dateModified = cursor.getLong(dateModifiedIdx)

                val displayNameIdx =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DISPLAY_NAME)
                val displayName = cursor.getString(displayNameIdx)

                val imgUrlIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_IMAGE_URL)
                val imgUrl = cursor.getString(imgUrlIdx)

                val preparedIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_PREPARED)
                val prepared = cursor.getInt(preparedIdx)

                books.add(
                    BookLocal(
                        id,
                        title,
                        displayName,
                        size,
                        data,
                        dateModified,
                        mimeType,
                        readingProgress = "0",
                        imgUrl ?: "",
                        prepared = prepared
                    )
                )
            } while (cursor.moveToNext())
            return books
        }

        override fun searchBookLocal(keyWord: String): List<BookLocal> {
            val condition = getSearchQueryConditions(keyWord)

            val cursor = readableDb.query(
                BookContract.BookEntry.TABLE_NAME,
                null,
                condition,
                null,
                null,
                null,
                null,
            )
            if (!cursor.moveToFirst()) return emptyList()
            val books = mutableListOf<BookLocal>()
            do {
                val idIdx = cursor.getColumnIndex(BaseColumns._ID)
                val id = cursor.getInt(idIdx)

                val titleIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_TITLE)
                val title = cursor.getString(titleIdx)

                val sizeIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_SIZE)
                val size = cursor.getLong(sizeIdx)

                val dataIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DATA)
                val data = cursor.getString(dataIdx)

                val mimeTypeIdx =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_MIME_TYPE)
                val mimeType = cursor.getString(mimeTypeIdx)

                val dateModifiedIdx =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DATE_MODIFIED)
                val dateModified = cursor.getLong(dateModifiedIdx)

                val displayNameIdx =
                    cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DISPLAY_NAME)
                val displayName = cursor.getString(displayNameIdx)

                val imgUrlIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_IMAGE_URL)
                val imgUrl = cursor.getString(imgUrlIdx)

                books.add(
                    BookLocal(
                        id,
                        title,
                        displayName,
                        size,
                        data,
                        dateModified,
                        mimeType,
                        readingProgress = "0",
                        imgUrl ?: ""
                    )
                )
            } while (cursor.moveToNext())

            return books
        }
    }

    val writableDAOObj = object : BookDbDAOWritableInterface {
        override fun insertBook(book: BookLocal, duplicateAction: InsertDuplicateAction): Boolean {
            var result = BookDbHelper.SQL_EXECUTION_FAILED
            when (duplicateAction) {
                InsertDuplicateAction.DUPLICATE -> {
                    result = writableDb.insert(
                        BookContract.BookEntry.TABLE_NAME,
                        null,
                        book.toContentValues()
                    )
                }
                InsertDuplicateAction.OVERRIDE -> {
                    val selection = "${BookContract.BookEntry.COLUMN_NAME_DATA} = ?"
                    val selectionArgs = arrayOf(book.data)
                    val findDuplicateCursor = readableDb.query(
                        BookContract.BookEntry.TABLE_NAME,
                        null,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                    )
                    if (foundItem(findDuplicateCursor)) {
                        val dataIdx =
                            findDuplicateCursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DATA)
                        val data = findDuplicateCursor.getString(dataIdx)
                        result = updateBook(
                            "${BookContract.BookEntry.COLUMN_NAME_DATA} = ?",
                            arrayOf(data),
                            newBook = book
                        ).toLong()
                    } else result = BookDbHelper.SQL_EXECUTION_FAILED
                }
                else -> {
                    val findDuplicateCursor = getDuplicatedCursor(book)
                    if (!foundItem(findDuplicateCursor)) {
                        result = writableDb.insert(
                            BookContract.BookEntry.TABLE_NAME,
                            null,
                            book.toContentValues()
                        )
                    }
                }
            }

            return result != BookDbHelper.SQL_EXECUTION_FAILED
        }

        override fun insertBooks(
            books: List<BookLocal>,
            duplicateAction: InsertDuplicateAction
        ): Boolean {
            books.forEach { insertBook(it, duplicateAction) }
            /** Should handle returned values more properly*/
            return true
        }


        override fun clearNotExistItems(books: List<BookLocal>): Boolean {
            val listTitles = books.map { it.title }
            val cursor = readableDb.query(
                BookContract.BookEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
            )
            if (!cursor.moveToFirst()) return false
            do {
                val titleIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_TITLE)
                val title = cursor.getString(titleIdx)

                if (!(title in listTitles)) {
                    deleteBook(title)
                }

            } while (cursor.moveToNext())

            return true
        }

        override fun deleteBook(title: String): Boolean {
            val selection = "${BookContract.BookEntry.COLUMN_NAME_TITLE} = ?"
            val selectionArgs = arrayOf(title)
            val deletedRows =
                writableDb.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs)
            return deletedRows != BookDbHelper.SQL_EXECUTION_FAILED_INT
        }

        override fun updateBook(book: BookLocal, newBook: BookLocal): Int {
            val selection = "${BookContract.BookEntry.COLUMN_NAME_DATA} = ?"
            val selectionArgs = arrayOf(book.data)

            return updateBook(selection, selectionArgs, newBook)
        }

        override fun updateBook(
            selection: String,
            selectionArgs: Array<String>,
            newBook: BookLocal
        ): Int {
            val result = writableDb.update(
                BookContract.BookEntry.TABLE_NAME,
                newBook.toContentValues(),
                selection,
                selectionArgs
            )
            return result
        }

        override fun updateImageUrl(res: Resource, fileTitle: String, imgUrl: String): Boolean {
            val selection = "${BookContract.BookEntry.COLUMN_NAME_TITLE} = ?"
            val selectionArgs = arrayOf(fileTitle)
            return updateImageUrl(selection, selectionArgs, imgUrl)
        }

        override fun updateImageUrl(
            selection: String,
            selectionArgs: Array<String>,
            imgUrl: String
        ): Boolean {
            val result = writableDb.update(
                BookContract.BookEntry.TABLE_NAME,
                imgUrl.toImgUrlContentValue(),
                selection,
                selectionArgs,
            )
            return result != BookDbHelper.SQL_EXECUTION_FAILED_INT
        }

        override fun markBookAsPrepared(book: BookLocal): Boolean {
            val selection = "${BaseColumns._ID} = ?"
            val selectionArgs = arrayOf(book.id.toString())

            val result = writableDb.update(
                BookContract.BookEntry.TABLE_NAME,
                getContentValuesBookPrepared(),
                selection,
                selectionArgs
            )
            return result >= BookDbHelper.SQL_EXECUTION_MINIMAL_IMPACT_NUMBER
        }

        private fun getContentValuesBookPrepared(): ContentValues {
            return ContentValues().apply {
                put(BookContract.BookEntry.COLUMN_NAME_PREPARED, BookLocal.PREPARED)
            }
        }
    }

    private fun getDuplicatedCursor(book: BookLocal): Cursor? {
        val selection = "${BookContract.BookEntry.COLUMN_NAME_DATA} = ?"
        val selectionArgs = arrayOf(book.data)
        val findDuplicateCursor = readableDb.query(
            BookContract.BookEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        return findDuplicateCursor
    }

    private fun foundItem(findDuplicateCursor: Cursor?): Boolean {
        return findDuplicateCursor != null && findDuplicateCursor.moveToFirst()
    }

    private fun getSearchQueryConditions(keyword: String): String {
        val conditionBuilder = StringBuilder("")
        val listWords = keyword.split(' ')
        var first = true
        var or = ""
        BookContract.SearchableBookEntry.values().forEach {
            listWords.forEach { word ->
                if (!first) {
                    or = "OR"
                }
                conditionBuilder.append(
                    "${or} ${it.value} LIKE '%${word}%' "
                )
                first = false
            }
        }
        return conditionBuilder.toString()
    }

    fun getBooksWithLimit(limit: Int): List<BookLocal> {
        val books = mutableListOf<BookLocal>()

        val cursor = readableDb.query(
            BookContract.BookEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null,
            "$limit"
        )

        if (!cursor.moveToFirst()) return emptyList()

        do {
            val idIdx = cursor.getColumnIndex(BaseColumns._ID)
            val id = cursor.getInt(idIdx)

            val titleIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_TITLE)
            val title = cursor.getString(titleIdx)

            val sizeIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_SIZE)
            val size = cursor.getLong(sizeIdx)

            val dataIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DATA)
            val data = cursor.getString(dataIdx)

            val mimeTypeIdx =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_MIME_TYPE)
            val mimeType = cursor.getString(mimeTypeIdx)

            val dateModifiedIdx =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DATE_MODIFIED)
            val dateModified = cursor.getLong(dateModifiedIdx)

            val displayNameIdx =
                cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_DISPLAY_NAME)
            val displayName = cursor.getString(displayNameIdx)

            val imgUrlIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_IMAGE_URL)
            val imgUrl = cursor.getString(imgUrlIdx)

            val preparedIdx = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_NAME_PREPARED)
            val prepared = cursor.getInt(preparedIdx)

            books.add(
                BookLocal(
                    id,
                    title,
                    displayName,
                    size,
                    data,
                    dateModified,
                    mimeType,
                    readingProgress = "0",
                    imgUrl ?: "",
                    prepared = prepared
                )
            )
        } while (cursor.moveToNext())

        return books.toList()
    }


    companion object {
        private var instance: BookDbDAOImpl? = null

        fun getInstance(bookDbHelper: BookDbHelper) = synchronized(this) {
            instance ?: BookDbDAOImpl(bookDbHelper).also { instance = it }
        }

        enum class InsertDuplicateAction {
            DUPLICATE,
            OVERRIDE,
            KEEP_ORIGINAL
        }
    }
}
