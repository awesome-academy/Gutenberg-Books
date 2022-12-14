package vn.ztech.software.projectgutenberg.data.repository.source.remote.api

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
}
