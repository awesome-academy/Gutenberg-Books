package vn.ztech.software.projectgutenberg.data.repository.source.remote.api

import vn.ztech.software.projectgutenberg.data.model.Agent
import vn.ztech.software.projectgutenberg.data.repository.source.repository.book.BookDataSource

object APIQuery {
    fun getBookAPI(page: Int = 1) =
        APIConstants.getBaseURLBuilder()
            .appendPath(APIConstants.PATH_BOOK)
            .appendQueryParameter(APIConstants.PARAM_PAGE, page.toString())
            .toString()

    fun getBookselfAPI(page: Int = 1) =
        APIConstants.getBaseURLBuilder()
            .appendPath(APIConstants.PATH_BOOKSHELF)
            .appendQueryParameter(APIConstants.PARAM_PAGE, page.toString())
            .toString()

    fun getBooksWithFilterAPI(
        page: Int = 1,
        filters: Map<BookDataSource.Companion.BookFilter, String>
    ): String {
        val result = APIConstants.getBaseURLBuilder()
            .appendPath(APIConstants.PATH_BOOK)
            .appendQueryParameter(APIConstants.PARAM_PAGE, page.toString())
        /**append filters*/
        filters.entries.forEach {
            result.appendQueryParameter(it.key.fullName, it.value.toString())
        }
        return result.toString()
    }

    fun getFiltersSameAuthor(agent: Agent): Map<BookDataSource.Companion.BookFilter, String> {
        val filters = mapOf(
            BookDataSource.Companion.BookFilter.AGENT_NAME_CONTAINS to agent.person,
            BookDataSource.Companion.BookFilter.HAS_AGENT_TYPE to agent.type
        )
        return filters
    }

}
