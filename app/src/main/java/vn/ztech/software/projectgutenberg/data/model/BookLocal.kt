package vn.ztech.software.projectgutenberg.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import vn.ztech.software.projectgutenberg.data.model.epub.EpubFile
import vn.ztech.software.projectgutenberg.data.model.epub.ResourceResponse
import vn.ztech.software.projectgutenberg.data.model.epub.TocItem
import vn.ztech.software.projectgutenberg.utils.Constant

@Parcelize
data class BookLocal(
    val id: Int = DEFAULT_ID,
    val title: String,
    val displayName: String,
    val size: Long,
    val data: String,
    val dateModified: Long,
    val mimeType: String,
    var readingProgress: String = DEFAULT_PROGRESS,
    val imageUrl: String = DEFAULT_IMG_URL,
    var isProcessing: Boolean = false,
    var epubFile: EpubFile? = null,
    var prepared: Int = NOT_PREPARED
) : Parcelable {
    fun getResourceResponse(url: String?): ResourceResponse? {
        url?.let {
            epubFile?.let {
                it.getResourceResponse(url)
            }
        }
        return null
    }

    fun getTocItem(id: Int): TocItem? {
        return epubFile?.toc?.listItem?.getOrNull(id)
    }

    fun getRecentReadingToc(): TocItem? {
        return epubFile?.getRecentReadingToc()
    }

    fun getTocByWebViewUrl(url: String): TocItem? {
        return epubFile?.getTocByWebViewUrl(url)
    }

    fun getReadingPercentage(): Int? {
        return epubFile?.getTotalPercentage()
    }
    fun isSupported(): Boolean{
        Constant.supportedResources.forEach {
            if (this.mimeType.contains(it.fullName)) return true
        }
        return false
    }
    companion object {
        const val PREPARED = 1
        const val NOT_PREPARED = 0
        const val DEFAULT_ID = 0
        const val DEFAULT_PROGRESS = "0"
        const val DEFAULT_IMG_URL = ""
    }
}
