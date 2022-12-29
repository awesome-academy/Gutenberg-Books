package vn.ztech.software.projectgutenberg.data.model.epub

import java.io.InputStream

data class ResourceResponse(
    val mimeType: String,
    val data: InputStream
)
