package vn.ztech.software.projectgutenberg.data.model.epub

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Manifest(
    private val items: MutableList<ManifestItem> = mutableListOf(),
    private val idToManifestItem: MutableMap<String, ManifestItem> = hashMapOf()
) : Parcelable {

    fun add(item: ManifestItem) {
        items.add(item)
        idToManifestItem[item.id] = item
    }

    fun clear() {
        items.clear()
    }

    fun findItemById(id: String): ManifestItem? = idToManifestItem[id]

    fun findByHref(href: String): ManifestItem? = items.firstOrNull() { it.href == href }
}
