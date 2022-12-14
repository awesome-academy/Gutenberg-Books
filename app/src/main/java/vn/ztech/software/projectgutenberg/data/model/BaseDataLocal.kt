package vn.ztech.software.projectgutenberg.data.model

data class BaseDataLocal<T>(
    var next: Boolean? = null,
    var results: MutableList<T> = mutableListOf<T>()
)
