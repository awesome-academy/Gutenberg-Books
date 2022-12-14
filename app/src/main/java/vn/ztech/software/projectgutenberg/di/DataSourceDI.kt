package vn.ztech.software.projectgutenberg.di

import android.content.Context
import vn.ztech.software.projectgutenberg.data.repository.source.local.BookLocalReadableDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.local.BookLocalWritableDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.BookFilesProvider
import vn.ztech.software.projectgutenberg.data.repository.source.remote.BookRemoteDataSource
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource

fun getBookRemoteDataSource(): BookDataSource.Remote {
    return BookRemoteDataSource.getInstance()
}

fun getBookLocalReadableDataSource(context: Context?): BookDataSource.Local.Readable {
    return BookLocalReadableDataSource.getInstance(
        getBookDBDAO(context),
        getBookReadingDBDAO(context),
        getBookFilesContentProvider(context)
    )
}

fun getBookFilesContentProvider(context: Context?): BookFilesProvider {
    return BookFilesProvider.getInstance(context)
}


fun getBookLocalWritableDataSource(context: Context?): BookDataSource.Local.Writable {
    return BookLocalWritableDataSource.getInstance(
        getBookDBDAO(context),
        getBookReadingDBDAO(context),
        getBookFilesContentProvider(context)
    )
}
