package vn.ztech.software.projectgutenberg.data.model

data class BaseAPIResponse<T>(
    val count: Int? = null,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T> = listOf()
)

object BaseAPIResponseEntry {
    const val COUNT = "count"
    const val NEXT = "next"
    const val PREVIOUS = "previous"
    const val RESULTS = "results"
}
