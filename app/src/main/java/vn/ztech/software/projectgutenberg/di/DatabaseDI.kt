package vn.ztech.software.projectgutenberg.di

import android.content.Context
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.BookDbHelper
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookDbDAO
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.bookreading.BookReadingDbDAO

fun getBookDBDAO(context: Context?): BookDbDAO {
    return BookDbDAO.getInstance(
        getBookDbHelper(context)
    )
}

fun getBookReadingDBDAO(context: Context?): BookReadingDbDAO {
    return BookReadingDbDAO.getInstance(
        getBookDbHelper(context)
    )
}

fun getBookDbHelper(context: Context?): BookDbHelper {
    return BookDbHelper.getInstance(context)
}
