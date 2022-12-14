package vn.ztech.software.projectgutenberg.data.repository.source.remote.utils

import org.json.JSONObject
import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponseEntry
import vn.ztech.software.projectgutenberg.utils.extension.getObjectArray

class ParseDataWithJson<T> {
    fun parseJsonToBaseAPIResponse(
        jsonObject: JSONObject?,
        keyEntity: String
    ): BaseAPIResponse<T>? {
        val count = jsonObject?.getInt(BaseAPIResponseEntry.COUNT)
        val next = jsonObject?.getString(BaseAPIResponseEntry.NEXT)
        val previous = jsonObject?.getString(BaseAPIResponseEntry.PREVIOUS)
        val resultsJsonArray = jsonObject?.getJSONArray(BaseAPIResponseEntry.RESULTS)
        val resultObjects = resultsJsonArray?.getObjectArray<T>(keyEntity)
        resultObjects?.let {
            return BaseAPIResponse(
                count,
                next,
                previous,
                results = it
            )
        }
        return null
    }
}
