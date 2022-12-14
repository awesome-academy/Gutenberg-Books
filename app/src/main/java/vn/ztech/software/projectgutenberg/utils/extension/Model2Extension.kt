package vn.ztech.software.projectgutenberg.utils.extension

import vn.ztech.software.projectgutenberg.data.model.BaseAPIResponse
import vn.ztech.software.projectgutenberg.data.model.BaseData
import vn.ztech.software.projectgutenberg.data.model.BaseDataLocal
import vn.ztech.software.projectgutenberg.data.model.BaseDatabaseResponse
import vn.ztech.software.projectgutenberg.data.model.BookLocal
import vn.ztech.software.projectgutenberg.data.model.Resource
import vn.ztech.software.projectgutenberg.utils.Constant

fun List<Resource>.getResourcesWithKind(): List<Resource> {

    val resources = mutableListOf<Resource>()
    this.forEach lit@{ resource ->
        Constant.ResourceKindClues.values().forEach {
            if (isNotDefaultType(
                    resource.type,
                    resource.uri
                ) && ((resource.type.contains(it.fullName)))
            ) {
                resource.actionType = it.actionType
                resource.kindShort = it.nameLowerCase

                resources.add(resource)
                return@lit
            }
        }
    }
    return resources.sortedBy { it.actionType }
}

private fun isNotDefaultType(type: String, uri: String): Boolean {
    Constant.ResourceKindClues.values()
        .filter { it.actionType == Constant.ActionTypes.DEFAULT }
        .forEach { if (type.contains(it.fullName) || uri.contains(it.fullName)) return false }
    return true
}

fun <T> BaseAPIResponse<T>.toBaseData(): BaseData<T> {
    val count = this.count
    val previous = this.previous
    val next = this.next
    val result = this.results

    return BaseData(count, next, previous, result.toMutableList())
}

fun <T> BaseDatabaseResponse<T>.toBaseDataLocal(): BaseDataLocal<T> {
    val next = this.next
    val result = this.results

    return BaseDataLocal(next, result.toMutableList())
}

fun <T> List<T>.toBaseDataLocal(enableNextPage: Boolean? = null): BaseDataLocal<T> {
    /**if enable next page is left blank, next page will be calculated by comparing with default page size*/
    return BaseDataLocal(
        next = if (enableNextPage == null) false else (this.size == Constant.LOCAL_DATA_QUERY_PAGE_SIZE),
        this.toMutableList()
    )
}

fun BookLocal.equalsById(newBook: BookLocal): Boolean {
    return this.id == newBook.id
}

