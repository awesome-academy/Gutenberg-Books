package vn.ztech.software.projectgutenberg.data.repository.source.local

import android.content.Context
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.model.epub.Toc
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.deleteBook
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.fetchBooks
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.book.BookDbDAO
import vn.ztech.software.projectgutenberg.data.repository.source.local.database.bookreading.BookReadingDbDAO
import vn.ztech.software.projectgutenberg.data.repository.source.local.utils.AsynchronousCall
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource
import vn.ztech.software.projectgutenberg.utils.extension.getUnzipAbsolutePath
import vn.ztech.software.projectgutenberg.utils.file.FileUtils


class BookLocalDataSource(
    private val bookDbDAO: BookDbDAO,
    private val bookReadingDbDAO: BookReadingDbDAO
) : BookDataSource.Local {


    override fun getBooksLocal(offset: Int, listener: OnResultListener<List<BookLocal>>) {
        AsynchronousCall<List<BookLocal>>(
            {
                val result = bookDbDAO.readableDAOObj.getBooks(offset)
                result
            },
            listener
        )
    }

    override fun scanLocalStorage(context: Context, onResultListener: OnResultListener<Boolean>) {
        AsynchronousCall(
            {
                val booksInStorage = fetchBooks(context)
                bookDbDAO.writableDAOObj.clearNotExistItems(booksInStorage)
                val result = bookDbDAO.writableDAOObj.insertBooks(booksInStorage)
                result
            },
            onResultListener
        )
    }

    override fun updateBookImageUrl(res: Resource, fileTitle: String, imgUrl: String) {
        AsynchronousCall<Boolean>(
            {
                bookDbDAO.writableDAOObj.updateImageUrl(res, fileTitle, imgUrl)
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

                resultDeleteFromStorage = deleteBook(context, bookLocal)
                if (resultDeleteFromStorage)
                    resultDeleteFromSQLite = bookDbDAO.writableDAOObj.deleteBook(bookLocal.title)

                (!resultDeleteFromStorage || resultDeleteFromSQLite)
            },
            listener
        )
    }

    override fun searchBookLocal(keyword: String, listener: OnResultListener<List<BookLocal>>) {
        AsynchronousCall<List<BookLocal>>(
            {
                bookDbDAO.readableDAOObj.searchBookLocal(keyword)
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
                    FileUtils.unZip(book.data, getUnzipAbsolutePath(context, book.title))
                },
                onResultListener
            )
        }
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
                storeTocToDB(book, epubFile, onResultListener)
                markBookAsPrepared(book)
                epubFile
            },
            onResultListener
        )
    }

    override fun storeTocToDB(
        book: BookLocal,
        epubFile: EpubFile,
        onResultListener: OnResultListener<EpubFile>
    ) {
        bookReadingDbDAO.writableDAO.storeToc(book, epubFile)
    }

    override fun getToc(bookId: Int, onResultListener: OnResultListener<Toc>) {
        AsynchronousCall(
            {
                bookReadingDbDAO.readableDAO.getToc(bookId)
            },
            onResultListener
        )
    }


    override fun markBookAsPrepared(book: BookLocal) {
        bookDbDAO.writableDAOObj.markBookAsPrepared(book)
    }


    companion object {
        private var instance: BookLocalDataSource? = null

        fun getInstance(bookDbDAO: BookDbDAO, bookReadingDbDAO: BookReadingDbDAO) =
            synchronized(this) {
                instance ?: BookLocalDataSource(bookDbDAO, bookReadingDbDAO).also { instance = it }
            }
    }

}
