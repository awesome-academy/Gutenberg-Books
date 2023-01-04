package vn.ztech.software.projectgutenberg.di

import android.content.Context
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookRepository

fun getBookRepository(context: Context?): BookRepository {
    return BookRepository.getInstance(
        getBookRemoteDataSource(),
        getBookLocalReadableDataSource(context),
        getBookLocalWritableDataSource(context),
        getBookFilesContentProvider(context)
    )
}
