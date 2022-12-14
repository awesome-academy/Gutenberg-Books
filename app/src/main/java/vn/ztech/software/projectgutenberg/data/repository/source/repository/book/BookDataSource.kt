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

interface BookDataSource {

    interface Local {

        interface Writable {
            fun unzipBook(
                context: Context?,
                book: BookLocal,
                onResultListener: OnResultListener<String>
            )


            fun updateBookImageUrl(res: Resource, fileTitle: String, imgUrl: String)

            fun deleteBookLocal(
                context: Context,
                bookLocal: BookLocal,
                listener: OnResultListener<Boolean>
            )


            fun updateReadingProgress(
                bookId: Int,
                href: String,
                readingProgressString: String,
                listener: OnResultListener<Boolean>
            )

            fun updateLatestReadingTocItem(tocItem: TocItem)

            fun storeTocToDB(
                book: BookLocal,
                epubFile: EpubFile,
                onResultListener: OnResultListener<EpubFile>
            )

            fun markBookAsPrepared(book: BookLocal)
        }

        interface Readable {
            fun getBooksLocal(offset: Int, listener: OnResultListener<List<BookLocal>>)

            fun scanLocalStorage(
                context: Context, onResultListener: OnResultListener<Boolean>
                = object : OnResultListener<Boolean> {
                        override fun onSuccess(data: Boolean) { // todo leave blank
                        }

                        override fun onError(e: Exception?) { // todo leave blank
                        }
                    }
            )

            fun parseEpub(
                providerUnzippedBookDirectoryPath: String,
                book: BookLocal, onResultListener: OnResultListener<EpubFile>
            )

            fun searchBookLocal(keyword: String, listener: OnResultListener<List<BookLocal>>)

            fun getToc(bookId: Int, onResultListener: OnResultListener<Toc>)

            fun getAllRecentReadingBook(onResultListener: OnResultListener<List<BookLocal>>)

            fun getBookWithLimit(limit: Int, onResultListener: OnResultListener<List<BookLocal>>)
        }
    }

    interface Remote {

        fun getBooks(page: Int, listener: OnResultListener<BaseAPIResponse<Book>>)

        fun getBooksWithFilters(
            page: Int,
            filters: Map<BookFilter, String>,
            listener: OnResultListener<BaseAPIResponse<Book>>
        )
    }

    companion object {

        enum class BookFilter(val fullName: String) {
            AGENT_NAME_CONTAINS("agent_name_contains"),
            HAS_AGENT_TYPE("has_agent_type"),
            LANGUAGES("languages"),
            HAS_BOOKSHELF("has_bookshelf")
        }

    }
}
