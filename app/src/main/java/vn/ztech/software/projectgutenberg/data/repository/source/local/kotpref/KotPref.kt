package vn.ztech.software.projectgutenberg.data.repository.source.local.kotpref

import android.content.Context
import android.content.SharedPreferences
import android.nfc.FormatException
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import vn.ztech.software.projectgutenberg.data.model.ModelCommon
import vn.ztech.software.projectgutenberg.data.model.epub.Settings
import vn.ztech.software.projectgutenberg.utils.extension.parse

class KotPref {


    fun save(settings: Settings) {
        kotPrefSettings?.let {
            with(it.edit()) {
                putString(REF_NAME_SETTINGS_ALL, settings.toJsonString())
                commit()
            }
        }
    }

    fun getLatestSettings(): Settings? {
        kotPrefSettings?.let {
            val settingsJson = it.getString(REF_NAME_SETTINGS_ALL, "") ?: ""
            return try {
                JSONObject(settingsJson).parse<Settings>(ModelCommon.SETTINGS)
            } catch (e:  JSONException) {
                e.printStackTrace()
                null
            } catch (e: FormatException) {
                e.printStackTrace()
                null
            }
        }
        return null
    }

    companion object {
        private var instance: KotPref? = null
        var kotPrefSettings: SharedPreferences? = null

        fun getInstance(context: Context?) = synchronized(this) {
            instance ?: KotPref().also {
                instance = it
                kotPrefSettings = context?.getSharedPreferences(REF_NAME_SETTINGS, Context.MODE_PRIVATE)
            }
        }

        const val REF_NAME_SETTINGS = "settings"
        const val REF_NAME_SETTINGS_ALL = "settings_all"
    }
}
