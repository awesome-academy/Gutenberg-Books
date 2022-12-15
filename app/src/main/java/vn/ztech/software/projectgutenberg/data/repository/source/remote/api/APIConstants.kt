package vn.ztech.software.projectgutenberg.data.repository.source.remote.api

import android.net.Uri

/**
 * List api endpoints below are taken from : https://gnikdroy.pythonanywhere.com/api/
 *  "book": "https://gnikdroy.pythonanywhere.com/api/book/",
"bookshelf": "https://gnikdroy.pythonanywhere.com/api/bookshelf/",
"agent_type": "https://gnikdroy.pythonanywhere.com/api/agent_type/",
"person": "https://gnikdroy.pythonanywhere.com/api/person/",
"resource": "https://gnikdroy.pythonanywhere.com/api/resource/",
"agent": "https://gnikdroy.pythonanywhere.com/api/agent/",
"language": "https://gnikdroy.pythonanywhere.com/api/language/",
"subject": "https://gnikdroy.pythonanywhere.com/api/subject/"
 * */
object APIConstants {
    private const val SCHEME = "https"
    private const val AUTHORITY = "gnikdroy.pythonanywhere.com"
    private const val CONTENT = "/api"

    const val PATH_BOOK = "/book"
    const val PATH_BOOKSHELF = "/bookshelf"
    const val PATH_AGENT_TYPE = "/agent_type"
    const val PATH_PERSON = "/person"
    const val PATH_RESOURCE = "/resource"
    const val PATH_AGENT = "/agent"
    const val PATH_LANGUAGE = "/language"
    const val PATH_SUBJECT = "/subject"

    const val PARAM_PAGE = "page"

    fun getBaseURLBuilder(): Uri.Builder =
        Uri.Builder()
            .scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(CONTENT)
}
