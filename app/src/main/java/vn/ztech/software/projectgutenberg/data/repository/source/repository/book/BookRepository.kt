package vn.ztech.software.projectgutenberg.data.repository.source.repository.book

import android.content.Context
import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.BookFilesProvider

class BookRepository private constructor(
    private val remote: BookDataSource.Remote,
    private val localReadable: BookDataSource.Local.Readable,
    private val localWritable: BookDataSource.Local.Writable,
    private val bookFilesProvider: BookFilesProvider,
) {
    val remoteReposObj = object : BookDataSource.Remote {
        override fun getBooks(page: Int, listener: OnResultListener<BaseAPIResponse<Book>>) {
            remote.getBooks(page, listener)
        }

        override fun getBooksWithFilters(
            page: Int,
            filters: Map<BookDataSource.Companion.BookFilter, String>,
            listener: OnResultListener<BaseAPIResponse<Book>>,
        ) {
            remote.getBooksWithFilters(page, filters, listener)
        }
    }

    val localReadableObj = object : BookDataSource.Local.Readable {
        override fun getBooksLocal(offset: Int, listener: OnResultListener<List<BookLocal>>) {
            localReadable.getBooksLocal(offset, listener)
        }

        override fun scanLocalStorage(
            context: Context,
            onResultListener: OnResultListener<Boolean>
        ) {
            localReadable.scanLocalStorage(context, onResultListener)
        }

        override fun searchBookLocal(keyword: String, listener: OnResultListener<List<BookLocal>>) {
            localReadable.searchBookLocal(keyword, listener)
        }

        override fun parseEpub(
            providerUnzippedBookDirectoryPath: String,
            book: BookLocal,
            onResultListener: OnResultListener<EpubFile>,
        ) {
            localReadable.parseEpub(providerUnzippedBookDirectoryPath, book, onResultListener)
        }

        override fun getToc(bookId: Int, onResultListener: OnResultListener<Toc>) {
            localReadable.getToc(bookId, onResultListener)
        }


        override fun getAllRecentReadingBook(onResultListener: OnResultListener<List<BookLocal>>) {
            localReadable.getAllRecentReadingBook(onResultListener)
        }

        override fun getBookWithLimit(limit: Int, onResultListener: OnResultListener<List<BookLocal>>) {
            localReadable.getBookWithLimit(limit, onResultListener)
        }
    }

    val localWritableObj = object : BookDataSource.Local.Writable {
        override fun updateBookImageUrl(res: Resource, fileTitle: String, imgUrl: String) {
            localWritable.updateBookImageUrl(res, fileTitle, imgUrl)
        }

        override fun deleteBookLocal(
            context: Context,
            bookLocal: BookLocal,
            listener: OnResultListener<Boolean>
        ) {
            localWritable.deleteBookLocal(context, bookLocal, listener)
        }

        override fun unzipBook(
            context: Context?,
            book: BookLocal,
            onResultListener: OnResultListener<String>
        ) {
            localWritable.unzipBook(context, book, onResultListener)
        }

        override fun markBookAsPrepared(book: BookLocal) {
            localWritable.markBookAsPrepared(book)
        }

        override fun storeTocToDB(
            book: BookLocal,
            epubFile: EpubFile,
            onResultListener: OnResultListener<EpubFile>
        ) {
            localWritable.storeTocToDB(book, epubFile, onResultListener)
        }

        override fun updateReadingProgress(
            bookId: Int, href: String,
            readingProgressString: String,
            listener: OnResultListener<Boolean>
        ) {
            localWritable.updateReadingProgress(bookId, href, readingProgressString, listener)
        }

        override fun updateLatestReadingTocItem(tocItem: TocItem) {
            localWritable.updateLatestReadingTocItem(tocItem)
        }
    }

    companion object {
        private var instance: BookRepository? = null

        fun getInstance(remote: BookDataSource.Remote,
                        localReadable: BookDataSource.Local.Readable,
                        localWritable: BookDataSource.Local.Writable,
                        bookFilesProvider: BookFilesProvider) =
            synchronized(this) {
                instance ?: BookRepository(
                    remote,
                    localReadable,
                    localWritable,
                    bookFilesProvider).also { instance = it }
            }
    }
}
