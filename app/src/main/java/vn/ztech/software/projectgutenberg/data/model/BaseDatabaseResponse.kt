package vn.ztech.software.projectgutenberg.data.model

data class BaseDatabaseResponse<T>(
    val next: Boolean? = null,
    val results: List<T> = listOf()
)

object BaseDatabaseResponseEntry {
    const val NEXT = "next"
    const val RESULTS = "results"
}
