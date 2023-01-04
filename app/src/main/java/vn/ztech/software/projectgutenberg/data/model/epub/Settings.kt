package vn.ztech.software.projectgutenberg.data.model.epub

import org.json.JSONObject
import vn.ztech.software.projectgutenberg.R
import vn.ztech.software.projectgutenberg.screen.readbook.WebViewHorizontal
import vn.ztech.software.projectgutenberg.utils.Constant

data class Settings(
    var fontSize: Int = DEFAULT_VALUE_NOT_EXIST,
    var textColor: String = DEFAULT_VALUE_NOT_EXIST_STRING,
    var backgroundColor: String = DEFAULT_VALUE_NOT_EXIST_STRING
) {

    fun getScriptFontSize(fontSize: Int?): String {
        return if (fontSize != null) "d.style.fontSize=$fontSize+'px';\n"
        else ""
    }

    private fun getScriptTextColor(): String {
        return if (textColor != DEFAULT_VALUE_NOT_EXIST_STRING) "color: ${textColor};"
        else ""
    }

    private fun getScriptBackgroundColor(): String {
        return if (backgroundColor != DEFAULT_VALUE_NOT_EXIST_STRING) "background-color: ${backgroundColor};}"
        else ""
    }

    private fun getScriptDisableHoverColor(): String {
        return if (textColor != DEFAULT_VALUE_NOT_EXIST_STRING) " a:hover {color: ${textColor}}"
        else "} a:hover {color: ${TEXT_COLOR_HOVER_DEFAULT}}"
    }

    fun getScriptCSS(): String {
        val sb = StringBuilder()
        if (textColor != DEFAULT_VALUE_NOT_EXIST_STRING) {
            sb.append(getScriptTextColor())
        }

        if (backgroundColor != DEFAULT_VALUE_NOT_EXIST_STRING) {
            sb.append(getScriptBackgroundColor())
        }

        sb.append(getScriptDisableHoverColor())

        return sb.toString()
    }

    fun toJsonString(): String {
        val obj = JSONObject()
        obj.put(SettingsEntry.FONT_SIZE, this.fontSize)
        obj.put(SettingsEntry.TEXT_COLOR, this.textColor)
        obj.put(SettingsEntry.BACKGROUND_COLOR, this.backgroundColor)
        return obj.toString()
    }

    fun fromTextSizeToProgressPercentage(): Int {
        if (fontSize == DEFAULT_VALUE_NOT_EXIST)
            return (
                    (WebViewHorizontal.DEFAULT_TEXT_SIZE -
                    WebViewHorizontal.DEFAULT_MIN_TEXT_SIZE
                    ) *
                    Constant.ONE_HUNDRED_PERCENT
                    ) /
                    (WebViewHorizontal.DEFAULT_MAX_TEXT_SIZE -
                            WebViewHorizontal.DEFAULT_MIN_TEXT_SIZE
                    )

        return ((this.fontSize - WebViewHorizontal.DEFAULT_MIN_TEXT_SIZE) * Constant.ONE_HUNDRED_PERCENT) /
                (WebViewHorizontal.DEFAULT_MAX_TEXT_SIZE - WebViewHorizontal.DEFAULT_MIN_TEXT_SIZE)
    }

    fun chipIdToThemeColor(chipId: Int): Pair<String, String>? {
        if (chipId == -1) return null
        return chipId2Colors[chipId]
    }

    companion object {
        const val BASE_FUNCTION__SETTINGS_START_STRING = "function settings() {\n"
        const val BASE_FUNCTION_END_STRING = " return ''}"
        val DEFAULT_TEXT_COLOR = null
        val DEFAULT_BACKGROUND_COLOR = null
        val DEFAULT_TEXT_SIZE = null
        const val DEFAULT_VALUE_NOT_EXIST = -1
        const val DEFAULT_VALUE_NOT_EXIST_STRING = ""
        const val TEXT_COLOR_HOVER_DEFAULT = "#000000"
        val chipId2Colors = hashMapOf<Int, Pair<String, String>>().also {
            it[R.id.chipDark] = Pair("#ffffff", "#000000")
            it[R.id.chipLight] = Pair("#000000", "#ffffff")
            it[R.id.chipVintage] = Pair("#1A1915", "#C4BAA2")
        }
    }
}

object SettingsEntry {
    const val FONT_SIZE = "fontSize"
    const val TEXT_COLOR = "textColor"
    const val BACKGROUND_COLOR = "backgroundColor"
}
