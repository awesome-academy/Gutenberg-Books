package vn.ztech.software.projectgutenberg.data.model

data class BaseData<T>(
    var count: Int? = null,
    var next: String? = null,
    var previous: String? = null,
    var results: MutableList<T> = mutableListOf<T>()
)
