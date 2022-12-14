package vn.ztech.software.projectgutenberg.utils.file

import vn.ztech.software.projectgutenberg.utils.Constant
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FileUtils {
    fun unZip(srcUri: String, desUri: String): String {
        val file = File(desUri)
        if (!file.isDirectory) {
            file.mkdir()
        }

        val zipIS = ZipInputStream(FileInputStream(srcUri))
        try {
            loopUnzip(zipIS, desUri)
        } finally {
            zipIS.close()
        }
        return desUri
    }

    private fun loopUnzip(zipIS: ZipInputStream, desUri: String) {
        var zipEntry: ZipEntry? = null
        while (zipIS.nextEntry.also { zipEntry = it } != null) {
            zipEntry?.let { ze ->
                val path = desUri + File.separator + ze.name
                handlePath(ze, desUri)
                if (!ze.isDirectory) {
                    writeToFile(path, zipIS)

                }
            }
        }
    }

    private fun writeToFile(path: String, zipIS: ZipInputStream) {
        val fileOS = FileOutputStream(path, false)
        try {
            var c = zipIS.read()
            while (c != -1) {
                fileOS.write(c)
                c = zipIS.read()
            }
        } finally {
            zipIS.closeEntry()
            fileOS.close()
        }
    }

    private fun handlePath(ze: ZipEntry, basePath: String) {
        val name = ze.name
        val listDirs = name.split(File.separator)

        /**Get list dirs except the last one, the last one is the file name*/
        if (listDirs.size > 1) createDirsIfNotExist(
            listDirs.subList(0, listDirs.size - 1),
            basePath
        )
    }

    private fun createDirsIfNotExist(dirs: List<String>, basePath: String) {
        val dirPath = StringBuffer(basePath)
        dirs.forEach { dir ->
            dirPath.append("${File.separator}$dir")
            val file = File(dirPath.toString())
            if (!file.exists()) file.mkdir()
        }
    }

    object FileUtilsConstants {
        const val BUFFER_SIZE = 6 * Constant.SIZE_1KB
    }
}
