package vn.ztech.software.projectgutenberg.data.repository

import java.lang.Exception

interface OnResultListener<T> {
    fun onSuccess(data: T)
    fun onError(e: Exception?)
}
