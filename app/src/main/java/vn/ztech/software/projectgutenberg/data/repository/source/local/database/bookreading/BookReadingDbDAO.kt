package vn.ztech.software.projectgutenberg.data.repository.source.local.database.bookreading

import android.provider.BaseColumns
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.BookDbHelper

class BookReadingDbDAO private constructor(dbHelper: BookDbHelper) {
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
                val progress = cursor.getInt(progressIdx)



                listTocItems.add(
                    TocItem(
                        bookId,
                        idx,
                        title,
                        href,
                        mimeType,
                        progress,
                    )
                )
            } while (cursor.moveToNext())

            return Toc(listTocItems)
        }

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
                    "${tocItem.progress}" +
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
    }

    companion object {
        private var instance: BookReadingDbDAO? = null

        fun getInstance(bookDbHelper: BookDbHelper) = synchronized(this) {
            instance ?: BookReadingDbDAO(bookDbHelper).also { instance = it }
        }

        enum class InsertDuplicateAction {
            DUPLICATE,
            OVERRIDE,
            KEEP_ORIGINAL
        }
    }
}
