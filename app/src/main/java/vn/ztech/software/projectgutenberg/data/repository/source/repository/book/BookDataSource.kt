package vn.ztech.software.projectgutenberg.data.repository.source.repository.book

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener

interface BookDataSource {

    interface Local {
        fun getBooksLocal(listener: OnResultListener<List<Book>>)
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
