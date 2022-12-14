package vn.ztech.software.projectgutenberg.data.repository.source.remote.utils

import android.os.Handler
import android.os.Looper
import org.json.JSONException
import org.json.JSONObject
import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class NetworkCall<T>(
    private val urlString: String,
    private val callBack: OnResultListener<BaseAPIResponse<T>>,
    private val keyEntity: String
) {
    private val mExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val mHandler = Handler(Looper.getMainLooper())
    var data: BaseAPIResponse<T>? = null

    init {
        callAPI()
    }

    private fun callAPI() {
        mExecutor.execute {
            try {
                val responseJson = getJsonFromUrl(urlString)
                data = ParseDataWithJson<T>().parseJsonToBaseAPIResponse(
                    JSONObject(responseJson),
                    keyEntity
                )
                mHandler.post {
                    data?.let { callBack.onSuccess(it) }
                }
            } catch (e: JSONException) {
                mHandler.post {
                    callBack.onError(e)
                }
            } catch (e: IOException) {
                mHandler.post {
                    callBack.onError(e)
                }
            }
        }
        mExecutor.shutdown()
    }

    private fun getJsonFromUrl(urlString: String): String {
        val url = URL(urlString)
        val stringBuilder = StringBuilder()
        var httpURLConnection: HttpURLConnection? = null
        var bufferedReader: BufferedReader? = null
        try {
            httpURLConnection = url.openConnection() as? HttpURLConnection
            httpURLConnection?.run {
                connectTimeout = TIME_OUT
                readTimeout = TIME_OUT
                requestMethod = METHOD_GET
                connect()
            }

            bufferedReader = BufferedReader(InputStreamReader(url.openStream()))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } finally {
            bufferedReader?.close()
            httpURLConnection?.disconnect()
        }

        return stringBuilder.toString()
    }

    companion object {
        private const val TIME_OUT = 15000
        private const val METHOD_GET = "GET"
    }
}
