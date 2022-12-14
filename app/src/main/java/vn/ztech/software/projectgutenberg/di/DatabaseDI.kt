package vn.ztech.software.projectgutenberg.di

import android.content.Context
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.BookDbHelper
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookDbDAOImpl
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.bookreading.BookReadingDbDAOImpl

fun getBookDBDAO(context: Context?): BookDbDAOImpl {
    return BookDbDAOImpl.getInstance(
        getBookDbHelper(context)
    )
}

fun getBookReadingDBDAO(context: Context?): BookReadingDbDAOImpl {
    return BookReadingDbDAOImpl.getInstance(
        getBookDbHelper(context)
    )
}

fun getBookDbHelper(context: Context?): BookDbHelper {
    return BookDbHelper.getInstance(context)
}
