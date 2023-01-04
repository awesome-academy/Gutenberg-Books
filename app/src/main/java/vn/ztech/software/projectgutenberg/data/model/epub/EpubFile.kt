package vn.ztech.software.projectgutenberg.data.model.epub

import android.os.Parcelable
import android.sax.RootElement
import kotlinx.android.parcel.Parcelize
import org.xml.sax.ContentHandler
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.getInputStreamFromExternalStorage
import vn.ztech.software.projectgutenberg.utils.extension.concatPath
import vn.ztech.software.projectgutenberg.utils.extension.getParentPath
import vn.ztech.software.projectgutenberg.utils.xml.parseXmlResource

@Parcelize
class EpubFile(val basePath: String? = null) : Parcelable {
    private var opfFileName: String? = null
    private val manifest = Manifest()
    private val spine: MutableList<ManifestItem> = mutableListOf()
    private var tocId: String? = null

    /**TOC: table of contents*/
    private var tableOfContents = TableOfContents()
    var toc: Toc = Toc()
    fun parseEpub() {
        // clear everything
        opfFileName = null
        manifest.clear()
        tocId = null
        spine.clear()
        tableOfContents.clear()

        if (basePath == null) return
        parseXmlResource(
            basePath.concatPath(CONTAINER_XML_FILE_PATH),
            getXmlParserHandlerContainer()
        )

        opfFileName?.let {
            parseXmlResource(basePath.concatPath(it), getXmlParserHandlerOpf())
        }

        tocId?.let {
            val tocManifestItem: ManifestItem? = manifest.findItemById(it)
            if (tocManifestItem != null) {
                val tocFilePath: String = tocManifestItem.href
                val resolver = HrefResolver(opfFileName!!.getParentPath())
                parseXmlResource(
                    basePath.concatPath(tocFilePath),
                    tableOfContents.getTocFileParserHandler(resolver)
                )
            }
        }

        createTocFromSpinAndTocMetadata()
    }

    private fun getXmlParserHandlerContainer(): ContentHandler {
        // describe the relationship of the elements
        val root = RootElement(
            XML_NAMESPACE_CONTAINER,
            XML_ELEMENT_CONTAINER
        )
        val rootfiles = root.getChild(
            XML_NAMESPACE_CONTAINER,
            XML_ELEMENT_ROOTFILES
        )
        val rootfile = rootfiles.getChild(
            XML_NAMESPACE_CONTAINER,
            XML_ELEMENT_ROOTFILE
        )

        rootfile.setStartElementListener { attributes ->
            val mediaType: String? =
                attributes.getValue(XML_ATTRIBUTE_MEDIATYPE)
            if (mediaType != null && mediaType == "application/oebps-package+xml") {
                opfFileName =
                    attributes.getValue(XML_ATTRIBUTE_FULLPATH)
            }
        }
        return root.contentHandler
    }

    private fun getXmlParserHandlerOpf(): ContentHandler {
        // describe the relationship of the elements
        val root = RootElement(
            XML_NAMESPACE_PACKAGE,
            XML_ELEMENT_PACKAGE
        )
        val manifestTag = root.getChild(
            XML_NAMESPACE_PACKAGE,
            XML_ELEMENT_MANIFEST
        )
        val manifestItem = manifestTag.getChild(
            XML_NAMESPACE_PACKAGE,
            XML_ELEMENT_MANIFESTITEM
        )
        val spineTag = root.getChild(
            XML_NAMESPACE_PACKAGE,
            XML_ELEMENT_SPINE
        )
        val itemref = spineTag.getChild(
            XML_NAMESPACE_PACKAGE,
            XML_ELEMENT_ITEMREF
        )

        val resolver = HrefResolver(opfFileName!!.getParentPath())

        manifestItem.setStartElementListener { attributes ->
            manifest.add(ManifestItem(attributes, resolver))
        }

        // get name of Table of Contents file from the Spine
        spineTag.setStartElementListener { attributes ->
            tocId = attributes.getValue(XML_ATTRIBUTE_TOC)
        }

        itemref.setStartElementListener { attributes ->
            val temp: String? = attributes.getValue(XML_ATTRIBUTE_IDREF)
            if (temp != null) {
                val item: ManifestItem? = manifest.findItemById(temp)
                if (item != null) {
                    spine.add(item)
                }
            }
        }
        return root.contentHandler
    }

    /**We don't use the TOC from toc.ncx file, since
     *  the navPoints inside this file usually contains
     *  # which signal a segment inside an html file.
     * The reader of this app does not support those segments.
     * Actually, I don't know how to handle this.
     * On the other hand, it doesn't bring any benefit
     * for the user if I decided to support it thp -_-
     * */

    private fun createTocFromSpinAndTocMetadata() {
        val listTocItem = mutableListOf<TocItem>()
        var idx = 0
        spine.forEachIndexed { _, spineItem ->
            val navPoint = tableOfContents.findNavPointByHref(spineItem.href)
            if (navPoint != null) {
                listTocItem.add(
                    TocItem(
                        idx = idx, title = navPoint.navLabel,
                        href = spineItem.href, mimeType = spineItem.mediaType
                    )
                )
            } else {
                /**The one that doesn't appear in toc file might be the cover page, thus still need to add to toc*/
                listTocItem.add(TocItem(idx, href = spineItem.href))
            }
            idx++
        }

        toc.listItem.addAll(listTocItem)
    }

    override fun toString(): String {
        return " OPF: $opfFileName \n Toc: $tableOfContents \n Spine: $spine \n Manifest: $manifest"
    }

    fun getResourceResponse(url: String): ResourceResponse? {
        val tocItem = toc.listItem.firstOrNull() { url.contains(it.href) }
        tocItem?.let {
            val inputStream = getInputStreamFromExternalStorage(url)
            inputStream?.let {
                ResourceResponse(tocItem.mimeType, inputStream)
            }
        }
        return null
    }

    fun getRecentReadingToc(): TocItem {
        val tocItem = toc.listItem.firstOrNull() { it.isLatestReading == 1 }
        return tocItem ?: toc.listItem.first()
    }

    fun getTocByWebViewUrl(url: String): TocItem? {
        return toc.listItem.firstOrNull {
            url.contains(it.href)
        }
    }

    fun getTotalPercentage(): Int {
        if (toc.listItem.isEmpty()) return 0
        return toc.getTotalPercentage()
    }

    companion object {
        // the container XML
        const val XML_NAMESPACE_CONTAINER = "urn:oasis:names:tc:opendocument:xmlns:container"
        const val XML_ELEMENT_CONTAINER = "container"
        const val XML_ELEMENT_ROOTFILES = "rootfiles"
        const val XML_ELEMENT_ROOTFILE = "rootfile"
        const val XML_ATTRIBUTE_FULLPATH = "full-path"
        const val XML_ATTRIBUTE_MEDIATYPE = "media-type"

        // the .opf XML
        private const val XML_NAMESPACE_PACKAGE = "http://www.idpf.org/2007/opf"
        private const val XML_ELEMENT_PACKAGE = "package"
        private const val XML_ELEMENT_MANIFEST = "manifest"
        private const val XML_ELEMENT_MANIFESTITEM = "item"
        private const val XML_ELEMENT_SPINE = "spine"
        private const val XML_ATTRIBUTE_TOC = "toc"
        private const val XML_ELEMENT_ITEMREF = "itemref"
        private const val XML_ATTRIBUTE_IDREF = "idref"

        private const val CONTAINER_XML_FILE_PATH = "META-INF/container.xml"
    }
}
