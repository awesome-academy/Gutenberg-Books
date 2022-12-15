package vn.ztech.software.projectgutenberg.data.model

data class BaseAPIResponse<T>(
    val count: Int?,
    val next: String?,
    val previous: String?,
    val results: List<T>
)

object BaseAPIResponseEntry {
    const val COUNT = "count"
    const val NEXT = "next"
    const val PREVIOUS = "previous"
    const val RESULTS = "results"
}
