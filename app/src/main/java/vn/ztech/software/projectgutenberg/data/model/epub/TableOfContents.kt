package vn.ztech.software.projectgutenberg.data.model.epub

import android.os.Parcelable
import android.sax.Element
import android.sax.RootElement
import kotlinx.android.parcel.Parcelize
import org.xml.sax.ContentHandler

@Parcelize
class TableOfContents(private val navPoints: MutableList<NavPoint> = mutableListOf()) : Parcelable {
    private var currDepth = 0
    private var supportedDepth = 1
    private var hrefResolver: HrefResolver? = null
    val size
        get() = navPoints.size

    fun add(navPoint: NavPoint) {
        navPoints.add(navPoint)
    }

    fun clear() {
        navPoints.clear()
    }

    fun getNavPoint(index: Int): NavPoint {
        return navPoints[index]
    }

    fun getLatestPoint(): NavPoint? {
        return navPoints.lastOrNull()
    }

    fun findNavPointByHref(href: String): NavPoint? {
        return navPoints.firstOrNull() { it.content.contains(href) }
    }

    fun getTocFileParserHandler(resolver: HrefResolver): ContentHandler {
        val root = RootElement(XML_NAMESPACE_TABLE_OF_CONTENTS, XML_ELEMENT_NCX)
        val navMap = root.getChild(XML_NAMESPACE_TABLE_OF_CONTENTS, XML_ELEMENT_NAVMAP)
        val navPoint = navMap.getChild(XML_NAMESPACE_TABLE_OF_CONTENTS, XML_ELEMENT_NAVPOINT)
        hrefResolver = resolver
        addNavPointToParserHandler(navPoint)
        return root.contentHandler
    }

    private fun addNavPointToParserHandler(navPoint: Element) {
        val navLabel = navPoint.getChild(XML_NAMESPACE_TABLE_OF_CONTENTS, XML_ELEMENT_NAVLABEL)
        val text = navLabel.getChild(XML_NAMESPACE_TABLE_OF_CONTENTS, XML_ELEMENT_TEXT)
        val content = navPoint.getChild(XML_NAMESPACE_TABLE_OF_CONTENTS, XML_ELEMENT_CONTENT)
        navPoint.setStartElementListener { attributes ->
            add(NavPoint(attributes.getValue(XML_ATTRIBUTE_PLAYORDER)))
            // extend parser to handle another level of nesting if required
            if (supportedDepth == ++currDepth) {
                val child = navPoint.getChild(XML_NAMESPACE_TABLE_OF_CONTENTS, XML_ELEMENT_NAVPOINT)
                addNavPointToParserHandler(child)
                ++supportedDepth
            }
        }

        text.setEndTextElementListener { body -> getLatestPoint()?.navLabel = body }

        content.setStartElementListener { attributes ->
            hrefResolver?.let {
                getLatestPoint()?.content = (it.concatPath(attributes.getValue(XML_ATTRIBUTE_SCR)))
            }
        }

        navPoint.setEndElementListener { --currDepth }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        navPoints.forEach {
            sb.append("$it - ")
        }
        return sb.toString()
    }

    companion object {
        private const val XML_NAMESPACE_TABLE_OF_CONTENTS = "http://www.daisy.org/z3986/2005/ncx/"
        private const val XML_ELEMENT_NCX = "ncx"
        private const val XML_ELEMENT_NAVMAP = "navMap"
        private const val XML_ELEMENT_NAVPOINT = "navPoint"
        private const val XML_ELEMENT_NAVLABEL = "navLabel"
        private const val XML_ELEMENT_TEXT = "text"
        private const val XML_ELEMENT_CONTENT = "content"
        private const val XML_ATTRIBUTE_PLAYORDER = "playOrder"
        private const val XML_ATTRIBUTE_SCR = "src"

    }
}
