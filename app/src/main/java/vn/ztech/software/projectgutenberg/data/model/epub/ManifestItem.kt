package vn.ztech.software.projectgutenberg.data.model.epub

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.xml.sax.Attributes

@Parcelize
data class ManifestItem
constructor(val href: String, val id: String, val mediaType: String) : Parcelable {
    constructor(attrs: Attributes, hrefResolver: HrefResolver) : this(
        hrefResolver.concatPath(attrs.getValue(XML_ATTRIBUTE_HREF)),
        attrs.getValue(XML_ATTRIBUTE_ID),
        attrs.getValue(XML_ATTRIBUTE_MEDIA_TYPE)
    )

    companion object {
        private const val XML_ATTRIBUTE_ID = "id"
        private const val XML_ATTRIBUTE_HREF = "href"
        private const val XML_ATTRIBUTE_MEDIA_TYPE = "media-type"
    }
}
