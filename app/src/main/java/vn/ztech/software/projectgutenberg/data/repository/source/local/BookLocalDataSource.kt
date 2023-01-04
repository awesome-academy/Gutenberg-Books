package vn.ztech.software.projectgutenberg.data.repository.source.local

import android.content.Context
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.BookFilesProvider
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookDbDAOImpl
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.bookreading.BookReadingDbDAOImpl
import vn.ztech.software.projectgutenberg.data.repository.source.local.utils.AsynchronousCall
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource
import vn.ztech.software.projectgutenberg.utils.extension.getUnzipAbsolutePath
import vn.ztech.software.projectgutenberg.utils.file.FileUtils


class BookLocalReadableDataSource(
    private val bookDbDAOImpl: BookDbDAOImpl,
    private val bookReadingDbDAOImpl: BookReadingDbDAOImpl,
    private val bookFilesProvider: BookFilesProvider,
) : BookDataSource.Local.Readable {

        override fun getBooksLocal(offset: Int, listener: OnResultListener<List<BookLocal>>) {
            AsynchronousCall<List<BookLocal>>(
                {
                    val books = bookDbDAOImpl.readableDAOObj.getBooks(offset)
                    bookReadingDbDAOImpl.mapTocsToBooks(books)
                    books
                },
                listener
            )
        }


        override fun scanLocalStorage(
            context: Context,
            onResultListener: OnResultListener<Boolean>
        ) {
            AsynchronousCall(
                {
                    val booksInStorage = bookFilesProvider.fetchBooks()
                    bookDbDAOImpl.writableDAOObj.clearNotExistItems(booksInStorage)
                    val result = bookDbDAOImpl.writableDAOObj.insertBooks(booksInStorage)
                    result
                },
                onResultListener
            )
        }


        override fun parseEpub(
            providerUnzippedBookDirectoryPath: String,
            book: BookLocal,
            onResultListener: OnResultListener<EpubFile>,
        ) {
            AsynchronousCall(
                {
                    val epubFile = EpubFile(providerUnzippedBookDirectoryPath)
                    epubFile.parseEpub()
                    localWritableObj.storeTocToDB(book, epubFile, onResultListener)
                    localWritableObj.markBookAsPrepared(book)
                    epubFile
                },
                onResultListener
            )
        }

        override fun searchBookLocal(keyword: String, listener: OnResultListener<List<BookLocal>>) {
            AsynchronousCall<List<BookLocal>>(
                {
                    bookDbDAOImpl.readableDAOObj.searchBookLocal(keyword)
                },
                listener
            )
        }


        override fun getToc(bookId: Int, onResultListener: OnResultListener<Toc>) {
            AsynchronousCall(
                {
                    bookReadingDbDAOImpl.readableDAO.getToc(bookId)
                },
                onResultListener
            )
        }


        override fun getAllRecentReadingBook(onResultListener: OnResultListener<List<BookLocal>>) {
            AsynchronousCall(
                {
                    val recentReadingBooks = bookReadingDbDAOImpl.readableDAO.getAllRecentReadingBook()
                    recentReadingBooks
                },
                onResultListener
            )
        }


        override fun getBookWithLimit(
            limit: Int,
            onResultListener: OnResultListener<List<BookLocal>>
        ) {
            AsynchronousCall(
                {
                    val books = bookDbDAOImpl.getBooksWithLimit(limit)
                    bookReadingDbDAOImpl.mapTocsToBooks(books)
                },
                onResultListener
            )
        }

    val localWritableObj = object : BookDataSource.Local.Writable {


        override fun updateBookImageUrl(res: Resource, fileTitle: String, imgUrl: String) {
            AsynchronousCall<Boolean>(
                {
                    bookDbDAOImpl.writableDAOObj.updateImageUrl(res, fileTitle, imgUrl)
                },
                null
            )
        }

        override fun deleteBookLocal(
            context: Context,
            bookLocal: BookLocal,
            listener: OnResultListener<Boolean>
        ) {
            AsynchronousCall<Boolean>(
                {
                    var resultDeleteFromStorage = false
                    var resultDeleteFromSQLite = false

                    resultDeleteFromStorage = bookFilesProvider.deleteBook(bookLocal)
                    if (resultDeleteFromStorage)
                        resultDeleteFromSQLite = bookDbDAOImpl.writableDAOObj.deleteBook(bookLocal.title)

                    (!resultDeleteFromStorage || resultDeleteFromSQLite)
                },
                listener
            )
        }


        override fun unzipBook(
            context: Context?,
            book: BookLocal,
            onResultListener: OnResultListener<String>
        ) {
            context?.let {
                AsynchronousCall(
                    {
                        FileUtils.unZip(book.data, getUnzipAbsolutePath(book.title))
                    },
                    onResultListener
                )
            }
        }


        override fun storeTocToDB(
            book: BookLocal,
            epubFile: EpubFile,
            onResultListener: OnResultListener<EpubFile>
        ) {
            bookReadingDbDAOImpl.writableDAO.storeToc(book, epubFile)
        }


        override fun updateReadingProgress(
            bookId: Int,
            href: String,
            readingProgressString: String,
            listener: OnResultListener<Boolean>
        ) {
            AsynchronousCall(
                {
                    val toc = bookReadingDbDAOImpl.writableDAO.updateReadingProgress(
                        bookId,
                        href,
                        readingProgressString
                    )
                    toc
                },
                listener
            )
        }

        override fun updateLatestReadingTocItem(tocItem: TocItem) {
            AsynchronousCall(
                {
                    val toc = bookReadingDbDAOImpl.writableDAO.updateLatestReadingToc(tocItem)
                    toc
                },
            )
        }

        override fun markBookAsPrepared(book: BookLocal) {
            bookDbDAOImpl.writableDAOObj.markBookAsPrepared(book)
        }

    }

    companion object {
        private var instance: BookLocalReadableDataSource? = null

        fun getInstance(bookDbDAOImpl: BookDbDAOImpl,
                        bookReadingDbDAOImpl: BookReadingDbDAOImpl,
                        bookFilesProvider: BookFilesProvider) =
            synchronized(this) {
                instance ?: BookLocalReadableDataSource(
                    bookDbDAOImpl,
                    bookReadingDbDAOImpl,
                    bookFilesProvider).also { instance = it }
            }
    }
}

class BookLocalWritableDataSource(
    private val bookDbDAOImpl: BookDbDAOImpl,
    private val bookReadingDbDAOImpl: BookReadingDbDAOImpl,
    private val bookFilesProvider: BookFilesProvider,
) : BookDataSource.Local.Writable {

        override fun updateBookImageUrl(res: Resource, fileTitle: String, imgUrl: String) {
            AsynchronousCall<Boolean>(
                {
                    bookDbDAOImpl.writableDAOObj.updateImageUrl(res, fileTitle, imgUrl)
                },
                null
            )
        }

        override fun deleteBookLocal(
            context: Context,
            bookLocal: BookLocal,
            listener: OnResultListener<Boolean>
        ) {
            AsynchronousCall<Boolean>(
                {
                    var resultDeleteFromStorage = false
                    var resultDeleteFromSQLite = false

                    resultDeleteFromStorage = bookFilesProvider.deleteBook(bookLocal)
                    if (resultDeleteFromStorage)
                        resultDeleteFromSQLite = bookDbDAOImpl.writableDAOObj.deleteBook(bookLocal.title)

                    (!resultDeleteFromStorage || resultDeleteFromSQLite)
                },
                listener
            )
        }


        override fun unzipBook(
            context: Context?,
            book: BookLocal,
            onResultListener: OnResultListener<String>
        ) {
            context?.let {
                AsynchronousCall(
                    {
                        FileUtils.unZip(book.data, getUnzipAbsolutePath(book.title))
                    },
                    onResultListener
                )
            }
        }


        override fun storeTocToDB(
            book: BookLocal,
            epubFile: EpubFile,
            onResultListener: OnResultListener<EpubFile>
        ) {
            bookReadingDbDAOImpl.writableDAO.storeToc(book, epubFile)
        }


        override fun updateReadingProgress(
            bookId: Int,
            href: String,
            readingProgressString: String,
            listener: OnResultListener<Boolean>
        ) {
            AsynchronousCall(
                {
                    val toc = bookReadingDbDAOImpl.writableDAO.updateReadingProgress(
                        bookId,
                        href,
                        readingProgressString
                    )
                    toc
                },
                listener
            )
        }

        override fun updateLatestReadingTocItem(tocItem: TocItem) {
            AsynchronousCall(
                {
                    val toc = bookReadingDbDAOImpl.writableDAO.updateLatestReadingToc(tocItem)
                    toc
                },
            )
        }

        override fun markBookAsPrepared(book: BookLocal) {
            bookDbDAOImpl.writableDAOObj.markBookAsPrepared(book)
        }

    companion object {
        private var instance: BookLocalWritableDataSource? = null

        fun getInstance(bookDbDAOImpl: BookDbDAOImpl,
                        bookReadingDbDAOImpl: BookReadingDbDAOImpl,
                        bookFilesProvider: BookFilesProvider) =
            synchronized(this) {
                instance ?: BookLocalWritableDataSource(
                    bookDbDAOImpl,
                    bookReadingDbDAOImpl,
                    bookFilesProvider).also { instance = it }
            }
    }
}
