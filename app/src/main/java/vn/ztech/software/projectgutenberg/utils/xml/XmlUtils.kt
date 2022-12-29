package vn.ztech.software.projectgutenberg.utils.xml

import org.xml.sax.ContentHandler
import org.xml.sax.InputSource
import vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider.getInputStreamFromExternalStorage
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

fun parseXmlResource(srcPath: String, handler: ContentHandler) {
    val inputStream = getInputStreamFromExternalStorage(srcPath)
    if (inputStream != null) {
        parseXml(inputStream, handler)
    }
}


private fun parseXml(inputStream: InputStream?, handler: ContentHandler) {
    if (inputStream == null) return
    val saxParserFactory = SAXParserFactory.newInstance()
    val xmlReader = saxParserFactory.newSAXParser().xmlReader
    xmlReader.contentHandler = handler

    inputStream.use { ins ->
        val inputSource = InputSource(inputStream)
        inputSource.encoding = XmlUtilsConst.ENCODE_UTF8
        xmlReader.parse(inputSource)
    }
}

object XmlUtilsConst {
    const val ENCODE_UTF8 = "UTF-8"
}
