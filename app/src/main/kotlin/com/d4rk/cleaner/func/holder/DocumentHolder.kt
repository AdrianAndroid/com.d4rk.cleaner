package com.d4rk.cleaner.func.holder

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.runtime.Immutable
import androidx.core.net.toFile
import androidx.core.provider.DocumentsContractCompat.DocumentCompat
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.deleteRecursively
import com.anggrayudi.storage.file.extension
import com.anggrayudi.storage.file.getAbsolutePath
import com.anggrayudi.storage.file.getBasePath
import com.anggrayudi.storage.file.getStorageId
import com.anggrayudi.storage.file.getStorageType
import com.anggrayudi.storage.file.hasParent
import com.anggrayudi.storage.file.isEmpty
import com.anggrayudi.storage.file.isRawFile
import com.anggrayudi.storage.file.mimeType
import com.anggrayudi.storage.file.openInputStream
import com.anggrayudi.storage.file.openOutputStream
import com.d4rk.cleaner.R
import com.d4rk.cleaner.data.core.AppCoreManager
import com.d4rk.cleaner.func.misc.FileMimeType
import com.d4rk.cleaner.func.misc.FileSortingPrefs
import com.d4rk.cleaner.func.misc.SortingMethod
import com.d4rk.cleaner.func.misc.sortFoldersFirst
import com.d4rk.cleaner.func.misc.sortLargerFirst
import com.d4rk.cleaner.func.misc.sortName
import com.d4rk.cleaner.func.misc.sortNameRev
import com.d4rk.cleaner.func.misc.sortNewerFirst
import com.d4rk.cleaner.func.misc.sortOlderFirst
import com.d4rk.cleaner.func.misc.sortSmallerFirst
import com.d4rk.cleaner.func.tabs.Tab
import com.d4rk.cleaner.utils.extension.emptyString
import com.d4rk.cleaner.utils.extension.isNot
import com.d4rk.cleaner.utils.extension.toFormattedDate
import com.d4rk.cleaner.utils.extension.toFormattedSize
import com.d4rk.cleaner.utils.helpers.FileSizeHelper
import java.io.File

@Immutable
data class DocumentHolder(val documentFile: DocumentFile) : ContentHolder(){
    companion object {
        const val UNKNOWN_NAME = "UNKNOWN"

        const val FILE_TYPE_FOLDER = -1
        const val FILE_TYPE_UNKNOWN = 0
        const val FILE_TYPE_AI = 1
        const val FILE_TYPE_APK = 2
        const val FILE_TYPE_CSS = 3
        const val FILE_TYPE_ISO = 4
        const val FILE_TYPE_JS = 5
        const val FILE_TYPE_PDF = 6
        const val FILE_TYPE_PSD = 7
        const val FILE_TYPE_SQL = 8
        const val FILE_TYPE_SVG = 9
        const val FILE_TYPE_VCF = 10
        const val FILE_TYPE_JAVA = 11
        const val FILE_TYPE_KOTLIN = 12
        const val FILE_TYPE_DOC = 13
        const val FILE_TYPE_XLS = 14
        const val FILE_TYPE_PPT = 15
        const val FILE_TYPE_FONT = 16
        const val FILE_TYPE_VECTOR = 17
        const val FILE_TYPE_VIDEO = 18
        const val FILE_TYPE_AUDIO = 19
        const val FILE_TYPE_IMAGE = 20
        const val FILE_TYPE_CODE = 21
        const val FILE_TYPE_TEXT = 22
        const val FILE_TYPE_ARCHIVE = 23

        fun fromFullPath(path: String): DocumentHolder? {
            val doc = DocumentFileCompat.fromFullPath(AppCoreManager.instance, path)
            return if (doc isNot null) DocumentHolder(doc!!) else null
        }

        fun fromFile(file: File): DocumentHolder {
            return DocumentHolder(DocumentFile.fromFile(file))
        }

        fun fromUri(uri: Uri): DocumentHolder? {
            val doc = DocumentFileCompat.fromUri(AppCoreManager.instance, uri)
            return if (doc isNot null) DocumentHolder(doc!!) else null
        }
    }
    var formattedDetailsCache = emptyString
    private val globalClass by lazy { AppCoreManager.instance }

    override fun getName() = documentFile.name ?: UNKNOWN_NAME
    override fun getContent() = documentFile

    val fileExtension by lazy { documentFile.extension.lowercase() }

    val fileSize by lazy { documentFile.length() }

    val lastModified by lazy { documentFile.lastModified() }

    val isHidden by lazy { getName().startsWith(".") }

    val uri by lazy { documentFile.uri }

    val path by lazy { documentFile.getAbsolutePath(globalClass) }

    val isFile by lazy { documentFile.isFile }

    val isFolder by lazy { documentFile.isDirectory }

    val isEmptyFile by lazy { documentFile.isDirectory.not() && documentFile.length() == 0L}
    val isEmptyDirectory by lazy { documentFile.isDirectory && documentFile.listFiles().isEmpty() }
    val isImage by lazy { isFile && FileMimeType.imageExtensions.contains(fileExtension) }
    val isVideo by lazy { isFile && FileMimeType.videoExtensions.contains(fileExtension) }
    val isAudio by lazy { isFile && FileMimeType.audioExtensions.contains(fileExtension) }
    val isOffice by lazy { isFile && FileMimeType.officeExtensions.contains(fileExtension) }
    val isArchive by lazy { isFile && FileMimeType.archiveExtensions.contains(fileExtension) }
    val isApk by lazy { isFile && FileMimeType.apkExtensions.contains(fileExtension) }
    val isFont by lazy { isFile && FileMimeType.fontExtensions.contains(fileExtension) }
    val isWindows by lazy { isFile && FileMimeType.windowsExtensions.contains(fileExtension) }
    val isGeneric by lazy { isFile && FileMimeType.genericExtensions.contains(fileExtension) }
//    val isApks by lazy { isFile && apkBundleFileType.contains(fileExtension) }

    val basePath by lazy { documentFile.getBasePath(globalClass) }

    val storageId by lazy { documentFile.getStorageId(globalClass) }

    val mimeType by lazy { documentFile.mimeType ?: getMimeType(uri) ?: FileMimeType.anyFileType }

    val canAccessParent by lazy { documentFile.parentFile != null || parentAlt != null }

    val parent: DocumentHolder? by lazy {
        documentFile.parentFile?.let { DocumentHolder(it) } ?: parentAlt
    }

    private val parentAlt: DocumentHolder? by lazy {
        val file = File(path)
        if (file.exists() && file.canRead()) {
            file.parentFile?.takeIf { it.exists() && it.canRead() }?.let {
                DocumentHolder(DocumentFile.fromFile(it))
            }
        } else null
    }

    fun exists() = documentFile.exists()

    fun isEmpty() = documentFile.isEmpty(globalClass)

    fun hasParent(parent: DocumentHolder): Boolean =
        documentFile.hasParent(globalClass, parent.documentFile)

    private fun getMimeType(uri: Uri): String? = MimeTypeMap
        .getSingleton()
        .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(uri.toString()))

    fun findFile(name: String): DocumentHolder? =
        documentFile.findFile(name)?.let { DocumentHolder(it) }

    fun listContent(
        sort: Boolean = true,
        sortingPrefs: FileSortingPrefs = FileSortingPrefs(),
        onFile: (DocumentHolder) -> Unit = { }
    ): ArrayList<DocumentHolder> {
        return arrayListOf<DocumentHolder>().apply {
            documentFile.listFiles().forEach {
                DocumentHolder(it).let { file ->
                    add(file)
                    onFile(file)
                }
            }

            if (sort) {
                when (sortingPrefs.sortMethod) {
                    SortingMethod.SORT_BY_NAME -> {
                        sortWith(if (sortingPrefs.reverseSorting) sortNameRev else sortName)
                    }

                    SortingMethod.SORT_BY_DATE -> {
                        sortWith(if (sortingPrefs.reverseSorting) sortOlderFirst else sortNewerFirst)
                    }

                    SortingMethod.SORT_BY_SIZE -> {
                        sortWith(if (sortingPrefs.reverseSorting) sortLargerFirst else sortSmallerFirst)
                    }
                }

                if (sortingPrefs.showFoldersFirst) sortWith(sortFoldersFirst)
            }
        }
    }

    fun walk(includeNoneEmptyFolders: Boolean = false): List<DocumentHolder> {
        val fileTree = mutableListOf<DocumentHolder>()
        listContent(false) {
            if (it.isFolder) {
                if (it.isEmpty()) {
                    fileTree.add(it)
                } else {
                    if (includeNoneEmptyFolders) fileTree.add(it)
                    fileTree.addAll(it.walk(includeNoneEmptyFolders))
                }
            } else {
                fileTree.add(it)
            }
        }
        return fileTree
    }

    fun delete() = documentFile.delete()

    fun deleteRecursively() = documentFile.deleteRecursively(globalClass)

    fun openInputStream() = documentFile.openInputStream(globalClass)

    fun openOutputStream() = documentFile.openOutputStream(globalClass)

    fun createSubFile(name: String) =
        documentFile.createFile(FileMimeType.anyFileType, name)?.let { DocumentHolder(it) }

    fun createSubFolder(name: String) =
        documentFile.createDirectory(name)?.let { DocumentHolder(it) }

    fun renameTo(newName: String) {
        documentFile.renameTo(newName)
    }

    fun getFormattedDetails(
        useCache: Boolean = false,
        showFolderContentCount: Boolean
    ): String {
        if (useCache && formattedDetailsCache.isNotEmpty()) {
            return formattedDetailsCache
        }

        val separator = " | "

        formattedDetailsCache = buildString {
            append(documentFile.lastModified().toFormattedDate())
            if (showFolderContentCount) append(separator)
            if (documentFile.isFile) {
                if (!showFolderContentCount) append(separator)
                append(documentFile.length().toFormattedSize())
                append(separator)
                append(fileExtension)
            } else if (showFolderContentCount) {
                append(getFormattedFileCount())
            }
        }

        return formattedDetailsCache
    }

    fun toFile(): File? {
        if (documentFile.isRawFile) {
            return uri.toFile()
        }
        return null
    }

    fun writeText(text: String) {
        if (documentFile.isRawFile) {
            File(path).writeText(text)
        } else {
            documentFile.openOutputStream(globalClass, false)?.use {
                it.write(text.toByteArray())
            }
        }
    }

    fun readText() = documentFile.openInputStream(globalClass)?.bufferedReader()?.use { reader ->
        val text = reader.readText()
        reader.close()
        text
    } ?: emptyString

    fun readLines(
        maxLines: Int = -1,
        onReadLine: (index: Int, line: String) -> Unit
    ) = documentFile.openInputStream(globalClass)?.bufferedReader()?.use { reader ->
        var lineCount = 0
        reader.forEachLine { line ->
            if (maxLines in 1..lineCount) return@forEachLine
            onReadLine(lineCount, line)
            lineCount++
        }
    } ?: emptyString

    fun appendText(text: String) {
        if (documentFile.isRawFile) {
            File(path).appendText(text)
        } else {
            documentFile.openOutputStream(globalClass, true)?.use {
                it.write(text.toByteArray())
            }
        }
    }

    private fun getFormattedFileCount(): String {
        var filesCount = 0
        var foldersCount = 0

        listContent(false) {
            if (it.isFile) filesCount++ else foldersCount++
        }

        return getFormattedFileCount(filesCount, foldersCount)
    }

    fun getFormattedFileCount(filesCount: Int, foldersCount: Int): String {
        return buildString {
            if (foldersCount == 0 && filesCount == 0) {
                append(globalClass.getString(R.string.empty_folder))
            } else {
                if (foldersCount > 0) {
                    append(globalClass.getString(R.string.folders_count, foldersCount))
                    if (filesCount > 0) append(", ")
                }
                if (filesCount > 0) {
                    append(globalClass.getString(R.string.files_count, filesCount))
                }
            }
        }
    }

    fun getFileIconType(): Int {
        if (isFolder) {
            return FILE_TYPE_FOLDER
        } else if (fileExtension == FileMimeType.aiFileType) {
            return FILE_TYPE_AI
        } else if (fileExtension == FileMimeType.apkFileType) {
            return FILE_TYPE_APK
        } else if (fileExtension == FileMimeType.cssFileType) {
            return FILE_TYPE_CSS
        } else if (fileExtension == FileMimeType.isoFileType) {
            return FILE_TYPE_ISO
        } else if (fileExtension == FileMimeType.jsFileType) {
            return FILE_TYPE_JS
        } else if (fileExtension == FileMimeType.psdFileType) {
            return FILE_TYPE_PSD
        } else if (fileExtension == FileMimeType.sqlFileType) {
            return FILE_TYPE_SQL
        } else if (fileExtension == FileMimeType.svgFileType) {
            return FILE_TYPE_SVG
        } else if (fileExtension == FileMimeType.vcfFileType) {
            return FILE_TYPE_VCF
        } else if (fileExtension == FileMimeType.pdfFileType) {
            return FILE_TYPE_PDF
        } else if (FileMimeType.docFileType.contains(fileExtension)) {
            return FILE_TYPE_DOC
        } else if (FileMimeType.excelFileType.contains(fileExtension)) {
            return FILE_TYPE_XLS
        } else if (FileMimeType.pptFileType.contains(fileExtension)) {
            return FILE_TYPE_PPT
        } else if (FileMimeType.fontFileType.contains(fileExtension)) {
            return FILE_TYPE_FONT
        } else if (FileMimeType.vectorFileType.contains(fileExtension)) {
            return FILE_TYPE_VECTOR
        } else if (FileMimeType.archiveFileType.contains(fileExtension) || FileMimeType.apkBundleFileType.contains(fileExtension)) {
            return FILE_TYPE_ARCHIVE
        } else if (FileMimeType.videoFileType.contains(fileExtension)) {
            return FILE_TYPE_VIDEO
        } else if (FileMimeType.codeFileType.contains(fileExtension)) {
            return FILE_TYPE_CODE
        } else if (FileMimeType.editableFileType.contains(fileExtension)) {
            return FILE_TYPE_TEXT
        } else if (FileMimeType.imageFileType.contains(fileExtension)) {
            return FILE_TYPE_IMAGE
        } else if (FileMimeType.audioFileType.contains(fileExtension)) {
            return FILE_TYPE_AUDIO
        } else {
            return FILE_TYPE_UNKNOWN
        }
    }

    fun getFileIconResource() = when (getFileIconType()) {
        FILE_TYPE_FOLDER -> R.drawable.baseline_folder_24
        FILE_TYPE_AI -> R.drawable.ai_file_extension
        FILE_TYPE_APK -> R.drawable.apk_file_extension
        FILE_TYPE_CSS -> R.drawable.css_file_extension
        FILE_TYPE_ISO -> R.drawable.iso_file_extension
        FILE_TYPE_JS -> R.drawable.js_file_extension
        FILE_TYPE_PDF -> R.drawable.pdf_file_extension
        FILE_TYPE_PSD -> R.drawable.psd_file_extension
        FILE_TYPE_SQL -> R.drawable.sql_file_extension
        FILE_TYPE_SVG -> R.drawable.svg_file_extension
        FILE_TYPE_VCF -> R.drawable.vcf_file_extension
        FILE_TYPE_JAVA -> R.drawable.javascript_file_extension
        FILE_TYPE_KOTLIN -> R.drawable.css_file_extension
        FILE_TYPE_DOC -> R.drawable.doc_file_extension
        FILE_TYPE_XLS -> R.drawable.xls_file_extension
        FILE_TYPE_PPT -> R.drawable.ppt_file_extension
        FILE_TYPE_FONT -> R.drawable.font_file_extension
        FILE_TYPE_VECTOR -> R.drawable.vector_file_extension
        FILE_TYPE_VIDEO -> R.drawable.video_file_extension
        FILE_TYPE_AUDIO -> R.drawable.music_file_extension
        FILE_TYPE_IMAGE -> R.drawable.jpg_file_extension
        FILE_TYPE_CODE -> R.drawable.css_file_extension
        FILE_TYPE_TEXT -> R.drawable.txt_file_extension
        FILE_TYPE_ARCHIVE -> R.drawable.zip_file_extension
        else -> R.drawable.unknown_file_extension
    }

    fun fileName() : String {
        return documentFile.name ?: ""
    }

    fun fileSize() : Long {
        return documentFile.length()
    }

    fun extension(): String {
        return documentFile.extension
    }

    fun absolutePath(): String {
        return documentFile.getAbsolutePath(globalClass)
    }

    fun isDirectory(): Boolean {
        return documentFile.isDirectory
    }

    fun isBigFile(): Boolean {
        return documentFile.length() > 1024 * 1024 * 10
    }

    fun isNewFile(): Boolean {
        return documentFile.lastModified() > System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30
    }
}
