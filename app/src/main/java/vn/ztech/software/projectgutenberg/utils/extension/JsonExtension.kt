package vn.ztech.software.projectgutenberg.utils.extension

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.data.model.Agent
import vn.ztech.software.projectgutenberg.data.model.AgentEntry
import vn.ztech.software.projectgutenberg.data.model.Book
import vn.ztech.software.projectgutenberg.data.model.BookEntry
import vn.ztech.software.projectgutenberg.data.model.Bookshelf
import vn.ztech.software.projectgutenberg.data.model.BookshelfEntry
import vn.ztech.software.projectgutenberg.data.model.ModelCommon
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.data.model.ResourceEntry
import vn.ztech.software.projectgutenberg.data.model.epub.Settings
import vn.ztech.software.projectgutenberg.data.model.epub.SettingsEntry

fun <T> JSONObject.parse(keyEntity: String): T {
    return when (keyEntity) {
        ModelCommon.BOOK -> {
            val agentsJsonArray = this.getJSONArray(BookEntry.AGENTS)
            val bookshelvesJsonArray = this.getJSONArray(BookEntry.BOOKSHELVES)
            val description = this.getString(BookEntry.DESCRIPTION)
            val downloads = this.getInt(BookEntry.DOWNLOADS)
            val id = this.getInt(BookEntry.ID)
            val languagesJSONArray = this.getJSONArray(BookEntry.LANGUAGES)
            val license = this.getString(BookEntry.LICENSE)
            val resourcesJsonArray = this.getJSONArray(BookEntry.RESOURCES)
            val subjectsJsonArray = this.getJSONArray(BookEntry.SUBJECTS)
            val title = this.getString(BookEntry.TITLE)
            val agents = agentsJsonArray.getObjectArray<Agent>(ModelCommon.AGENT)
            val bookshelves = bookshelvesJsonArray.getStringArray()
            val languages = languagesJSONArray.getStringArray()
            val resources = resourcesJsonArray.getObjectArray<Resource>(ModelCommon.RESOURCE)
            val subjects = subjectsJsonArray.getStringArray()
            Book(
                agents, bookshelves, description, downloads, id,
                languages, license, resources, subjects, title
            ) as T
        }
        ModelCommon.BOOKSHELF -> {
            val id: Int = this.getInt(BookshelfEntry.ID)
            val name: String = this.getString(BookshelfEntry.NAME)
            Bookshelf(id, name) as T
        }
        ModelCommon.AGENT -> {
            val id: Int = this.getInt(AgentEntry.ID)
            val person: String = this.getString(AgentEntry.PERSON)
            val type: String = this.getString(AgentEntry.TYPE)

            Agent(id, person, type) as T
        }
        ModelCommon.RESOURCE -> {
            val id: Int = this.getInt(ResourceEntry.ID)
            val type: String = this.getString(ResourceEntry.TYPE)
            val uri: String = this.getString(ResourceEntry.URI)
            Resource(id, type, uri) as T
        }
        ModelCommon.SETTINGS -> {
            val fontSize: Int = this.optInt(SettingsEntry.FONT_SIZE, Settings.DEFAULT_VALUE_NOT_EXIST)
            val textColor: String = this.optString(SettingsEntry.TEXT_COLOR, Settings.DEFAULT_VALUE_NOT_EXIST_STRING)
            val backgroundColor: String = this.optString(
                SettingsEntry.BACKGROUND_COLOR,
                Settings.DEFAULT_VALUE_NOT_EXIST_STRING
            )
            Settings(fontSize, textColor, backgroundColor) as T
        }
        else -> throw JSONException(R.string.exception_parse_json.toString())
    }
}

fun <T> JSONArray.getObjectArray(keyEntity: String): List<T> {
    val data = mutableListOf<T>()
    for (i in 0 until this.length()) {
        val item = this.getJSONObject(i)
        data.add(item.parse<T>(keyEntity))
    }
    return data
}

fun JSONArray.getStringArray(): List<String> {
    val data = mutableListOf<String>()
    for (i in 0 until this.length()) {
        data.add(this.getString(i))
    }
    return data
}
