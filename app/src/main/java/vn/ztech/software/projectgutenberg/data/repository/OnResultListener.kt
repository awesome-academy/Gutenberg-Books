package vn.ztech.software.projectgutenberg.data.repository

interface OnResultListener<T> {
    fun onSuccess(data: T)
    fun onError(e: Exception?)
}
