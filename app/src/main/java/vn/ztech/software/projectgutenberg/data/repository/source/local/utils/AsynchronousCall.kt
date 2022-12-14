package vn.ztech.software.projectgutenberg.data.repository.source.local.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import vn.ztech.software.projectgutenberg.data.repository.OnResultListener
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AsynchronousCall<T>(val handle: () -> T, val listener: OnResultListener<T>? = null) {

    private val mExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val mHandler = Handler(Looper.getMainLooper())

    init {
        execute()
    }

    private fun execute() {
        mExecutor.execute {
            try {
                val result = handle()
                Log.d("THREADXXX", Thread.currentThread().name)
                mHandler.post {
                    listener?.onSuccess(result)
                }
            } catch (e: IOException) {
                mHandler.post {
                    listener?.onError(e)
                }
            }
        }
        mExecutor.shutdown()
    }
}
