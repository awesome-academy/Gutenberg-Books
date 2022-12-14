package vn.ztech.software.projectgutenberg.di

import android.content.Context
import vn.ztech.software.projectgutenberg.data.repository.source.local.BookLocalDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.remote.BookRemoteDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource

fun getBookRemoteDataSource(): BookDataSource.Remote {
    return BookRemoteDataSource.getInstance()
}

fun getBookLocalDataSource(context: Context?): BookDataSource.Local {
    return BookLocalDataSource.getInstance(
        getBookDBDAO(context)
    )
}
