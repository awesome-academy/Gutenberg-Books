package vn.ztech.software.projectgutenberg.data.model.epub

import vn.ztech.software.projectgutenberg.utils.extension.concatPath

class HrefResolver(private val parentPath: String) {
    fun concatPath(relativeHref: String): String = parentPath.concatPath(relativeHref)
}
