package vn.ztech.software.projectgutenberg.data.repository.source.local.contentprovider

import android.os.Environment
import vn.ztech.software.projectgutenberg.utils.Constant
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream

fun getProviderBasePath(): String {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path +
            File.separator +
            BookFilesProvider.APP_NAME
}


fun getProviderUnzippedBooksBaseDirectoryPath(): String {
    return getProviderBasePath() +
            File.separator +
            Constant.UNZIPPED_FOLDER_NAME
}
fun getProviderUnzippedBookDirectoryPath(title: String): String {
    return getProviderUnzippedBooksBaseDirectoryPath() +
            File.separator +
            title
}

fun getInputStreamFromExternalStorage(srcPath: String): InputStream? {
    return BufferedInputStream(File(srcPath).inputStream())
}
