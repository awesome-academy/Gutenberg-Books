package vn.ztech.software.projectgutenberg.data.repository.source.local.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookContract.BookEntry
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.bookreading.BookReadingContract.BookReadingEntry

class BookDbHelper private constructor(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
        db?.execSQL(SQL_CREATE_ENTRIES_BOOK_READING)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        db?.execSQL(SQL_DELETE_ENTRIES_BOOK_READING)
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Gutenberge.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${BookEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${BookEntry.COLUMN_NAME_TITLE} TEXT," +
                    "${BookEntry.COLUMN_NAME_DATA} TEXT," +
                    "${BookEntry.COLUMN_NAME_MIME_TYPE} TEXT," +
                    "${BookEntry.COLUMN_NAME_READING_PROGRESS} TEXT," +
                    "${BookEntry.COLUMN_NAME_DATE_MODIFIED} TEXT," +
                    "${BookEntry.COLUMN_NAME_DISPLAY_NAME} TEXT," +
                    "${BookEntry.COLUMN_NAME_IMAGE_URL} TEXT," +
                    "${BookEntry.COLUMN_NAME_PREPARED} INTEGER," +
                    "${BookEntry.COLUMN_NAME_SIZE} TEXT)"

        private const val SQL_CREATE_ENTRIES_BOOK_READING =
            "CREATE TABLE ${BookReadingEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER," +
                    "${BookReadingEntry.COLUMN_NAME_TOC_IDX} INTEGER," +
                    "${BookReadingEntry.COLUMN_NAME_TOC_ITEM_TITLE} TEXT," +
                    "${BookReadingEntry.COLUMN_NAME_TOC_ITEM_HREF} TEXT," +
                    "${BookReadingEntry.COLUMN_NAME_TOC_ITEM_MIME_TYPE} TEXT," +
                    "${BookReadingEntry.COLUMN_NAME_PROGRESS} TEXT," +
                    "${BookReadingEntry.COLUMN_NAME_IS_LATEST_READING} INTEGER," +
                    "PRIMARY KEY (${BaseColumns._ID}, ${BookReadingEntry.COLUMN_NAME_TOC_ITEM_HREF}))"

        /**Use id, toc_item_href as primary key*/

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $BookEntry.TABLE_NAME}"
        private val SQL_DELETE_ENTRIES_BOOK_READING =
            "DROP TABLE IF EXISTS ${BookReadingEntry}.TABLE_NAME}"

        const val SQL_EXECUTION_FAILED = -1L
        const val SQL_EXECUTION_FAILED_INT = -1
        const val SQL_EXECUTION_DELETE_ONE_SUCCESS = 1
        const val SQL_EXECUTION_MINIMAL_IMPACT_NUMBER = 1


        private var instance: BookDbHelper? = null

        fun getInstance(context: Context?) = synchronized(this) {
            instance ?: BookDbHelper(context).also { instance = it }
        }
    }
}
