package vn.ztech.software.projectgutenberg.utils.base

interface BasePresenter<T> {
    fun onStart()
    fun onStop()
    fun setView(view: T?)

    companion object {
        enum class Result {
            SUCCESS,
            FAILED
        }
    }
}
