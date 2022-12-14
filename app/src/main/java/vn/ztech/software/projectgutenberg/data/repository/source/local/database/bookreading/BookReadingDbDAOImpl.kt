package vn.ztech.software.projectgutenberg.data.repository.source.local.database.bookreading

import android.content.ContentValues
import android.provider.BaseColumns
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.BookDbHelper
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookContract

class BookReadingDbDAOImpl private constructor(dbHelper: BookDbHelper) {
    private val writableDb by lazy { dbHelper.writableDatabase }
    private val readableDb by lazy { dbHelper.readableDatabase }
    val readableDAO = object : BookReadingDbDAOReadableInterface {
        override fun getToc(_id: Int): Toc {
            val listTocItems = mutableListOf<TocItem>()

            val selection = "${BaseColumns._ID} = ?"
            val selectionArgs = arrayOf(_id.toString())
            val cursor = readableDb.query(
                BookReadingContract.BookReadingEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null,
            )
            if (!cursor.moveToFirst()) return Toc(mutableListOf())
            do {
                val bookIdIdx = cursor.getColumnIndex(BaseColumns._ID)
                val bookId = cursor.getInt(bookIdIdx)

                val idxIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_IDX)
                val idx = cursor.getInt(idxIdx)

                val titleIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_TITLE)
                val title = cursor.getString(titleIdx)

                val hrefIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_HREF)
                val href = cursor.getString(hrefIdx)

                val mimeTypeIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_HREF)
                val mimeType = cursor.getString(mimeTypeIdx)

                val progressIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_PROGRESS)
                val progress = cursor.getString(progressIdx)

                val isLatestReadingTocIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_IS_LATEST_READING)
                val isLatestReadingToc = cursor.getInt(isLatestReadingTocIdx)

                listTocItems.add(
                    TocItem(
                        bookId,
                        idx,
                        title,
                        href,
                        mimeType,
                        progress,
                        isLatestReading = isLatestReadingToc
                    )
                )
            } while (cursor.moveToNext())

            return Toc(listTocItems)
        }

        override fun getAllRecentReadingBook(): List<BookLocal> {
            val listTocItems = mutableListOf<TocItem>()

            val cursor = readableDb.query(
                BookReadingContract.BookReadingEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
            )
            if (!cursor.moveToFirst()) return emptyList()
            do {
                val bookIdIdx = cursor.getColumnIndex(BaseColumns._ID)
                val bookId = cursor.getInt(bookIdIdx)

                val idxIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_IDX)
                val idx = cursor.getInt(idxIdx)

                val titleIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_TITLE)
                val title = cursor.getString(titleIdx)

                val hrefIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_HREF)
                val href = cursor.getString(hrefIdx)

                val mimeTypeIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_HREF)
                val mimeType = cursor.getString(mimeTypeIdx)

                val progressIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_PROGRESS)
                val progress = cursor.getString(progressIdx)

                val isLatestReadingTocIdx =
                    cursor.getColumnIndex(BookReadingContract.BookReadingEntry.COLUMN_NAME_IS_LATEST_READING)
                val isLatestReadingToc = cursor.getInt(isLatestReadingTocIdx)

                listTocItems.add(
                    TocItem(
                        bookId,
                        idx,
                        title,
                        href,
                        mimeType,
                        progress,
                        isLatestReading = isLatestReadingToc
                    )
                )
            } while (cursor.moveToNext())

            val result = mutableListOf<BookLocal>()
            val books = listTocItems.groupBy { it.bookId }
            books.forEach { bookMap ->
                val book = getBookById(bookMap.key)
                book?.let {
                    it.epubFile = EpubFile().also { epubFile ->
                        epubFile.toc = Toc(bookMap.value.toMutableList())
                    }
                    result.add(book)
                }
            }
            return result
        }
    }

    private fun getBookById(mid: Int): BookLocal? {

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(mid.toString())

        val cursor = readableDb.query(
            BookContract.BookEntry.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null,
        )
        if (!cursor.moveToFirst()) return null
        var book: BookLocal? = null
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

            book = BookLocal(
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
        } while (cursor.moveToNext())
        return book
    }

    fun mapTocsToBooks(books: List<BookLocal>): List<BookLocal> {
        books.forEach { book ->
            book.epubFile = EpubFile().also { it.toc = readableDAO.getToc(book.id) }
        }
        return books
    }

    val writableDAO = object : BookReadingDbDAOWritableInterface {
        override fun insertOrIgnoreTocItem(book: BookLocal, tocItem: TocItem): Boolean {
            /**Insert toc item into the DB if it does not exist yet,
             *  but ignore if it does it preserve the reading history*/
            val tableName = BookReadingContract.BookReadingEntry.TABLE_NAME
            val rawQueryInsertOrIgnore = "" +
                    "INSERT OR IGNORE INTO $tableName " +
                    "(" +
                    "${BaseColumns._ID}," +
                    "${BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_IDX}," +
                    "${BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_TITLE}," +
                    "${BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_HREF}," +
                    "${BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_MIME_TYPE}," +
                    "${BookReadingContract.BookReadingEntry.COLUMN_NAME_PROGRESS}" +
                    ") " +
                    "VALUES(" +
                    "${book.id}," +
                    "${tocItem.idx}," +
                    "\"${tocItem.title}\"," +
                    "'${tocItem.href}'," +
                    "'${tocItem.mimeType}'," +
                    "'${tocItem.progress}'" +
                    ");"
            writableDb.execSQL(rawQueryInsertOrIgnore)
            return true
        }

        override fun storeToc(book: BookLocal, epubFile: EpubFile): Boolean {
            epubFile.toc.listItem.forEach { tocItem ->
                insertOrIgnoreTocItem(book, tocItem)
            }
            return true
        }

        override fun updateReadingProgress(
            bookId: Int,
            href: String,
            readingProgressString: String
        ): Boolean {
            val selection = "${BaseColumns._ID} = ? " +
                    "AND ${BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_HREF} = ?"
            val selectionArgs = arrayOf(bookId.toString(), href)
            val result = writableDb.update(
                BookReadingContract.BookReadingEntry.TABLE_NAME,
                readingProgressToContentValues(readingProgressString),
                selection,
                selectionArgs
            )

            return result >= BookDbHelper.SQL_EXECUTION_MINIMAL_IMPACT_NUMBER
        }

        override fun updateLatestReadingToc(tocItem: TocItem): Boolean {

            val selection = "${BaseColumns._ID} = ? " +
                    "AND ${BookReadingContract.BookReadingEntry.COLUMN_NAME_IS_LATEST_READING} = ? "
            val selectionArgs = arrayOf(tocItem.bookId.toString(), "1")
            val result = writableDb.update(
                BookReadingContract.BookReadingEntry.TABLE_NAME,
                latestReadingToContentValues(0),
                selection,
                selectionArgs
            )

            val selection2 = "${BaseColumns._ID} = ? " +
                    "AND ${BookReadingContract.BookReadingEntry.COLUMN_NAME_TOC_ITEM_HREF} = ?"
            val selectionArgs2 = arrayOf(tocItem.bookId.toString(), tocItem.href)
            val result2 = writableDb.update(
                BookReadingContract.BookReadingEntry.TABLE_NAME,
                latestReadingToContentValues(1),
                selection2,
                selectionArgs2
            )

            return result >= BookDbHelper.SQL_EXECUTION_MINIMAL_IMPACT_NUMBER &&
                    result2 >= BookDbHelper.SQL_EXECUTION_MINIMAL_IMPACT_NUMBER
        }
    }

    companion object {
        private var instance: BookReadingDbDAOImpl? = null

        fun getInstance(bookDbHelper: BookDbHelper) = synchronized(this) {
            instance ?: BookReadingDbDAOImpl(bookDbHelper).also { instance = it }
        }

        enum class InsertDuplicateAction {
            DUPLICATE,
            OVERRIDE,
            KEEP_ORIGINAL
        }

        fun readingProgressToContentValues(progress: String): ContentValues {
            return ContentValues().apply {
                put(BookReadingContract.BookReadingEntry.COLUMN_NAME_PROGRESS, progress)
            }
        }

        fun latestReadingToContentValues(value: Int): ContentValues {
            return ContentValues().apply {
                put(BookReadingContract.BookReadingEntry.COLUMN_NAME_IS_LATEST_READING, value)
            }
        }
    }
}
