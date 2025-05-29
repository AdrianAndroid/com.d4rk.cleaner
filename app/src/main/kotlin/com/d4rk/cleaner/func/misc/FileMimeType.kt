package com.d4rk.cleaner.func.misc

object FileMimeType {
    const val aiFileType = "ai"
    const val apkFileType = "apk"
    const val cssFileType = "css"
    const val isoFileType = "iso"
    const val jsFileType = "js"
    const val pdfFileType = "pdf"
    const val psdFileType = "psd"
    const val sqlFileType = "sql"
    const val svgFileType = "svg"
    const val vcfFileType = "vcf"
    const val javaFileType = "java"
    const val kotlinFileType = "kt"
    const val jsonFileType = "json"
    const val anyFileType = "*/*"

    @JvmField
    val genericExtensions = arrayOf(
        "DS_Store",
        "Spotlight-V100",
        "fseventsd",
        "log",
        "logs",
        "old",
        "tmp",
        "temp",
        "splashad",
        "db",
        "nomedia",
    )

    @JvmField
    val apkExtensions = arrayOf(
        "apk",
        "apks",
        "apkm",
        "apkx",
        "aab",
    )

    @JvmField
    val officeExtensions = arrayOf(
        "doc",
        "dot",
        "wbk",
        "docx",
        "docm",
        "dotx",
        "dotm",
        "docb",
        "pdf",
        "wll",
        "wwl",
        "xls",
        "xlt",
        "xlm",
        "xlsx",
        "xlsm",
        "xltx",
        "xltm",
        "xlsb",
        "xla",
        "xlam",
        "xll",
        "xlw",
        "ppt",
        "pot",
        "pps",
        "ppa",
        "pptx",
        "pptm",
        "potx",
        "potm",
        "ppam",
        "ppsx",
        "ppsm",
        "sldx",
        "sldm",
        "ppam",
        "accda",
        "accdb",
        "accde",
        "accdr",
        "accdt",
        "accdu",
        "one",
        "ecf",
        "pub",
        "txt",
    )

    @JvmField
    val archiveExtensions = arrayOf(
        "7z",
        "ace",
        "arj",
        "bz",
        "bz2",
        "cab",
        "cb7",
        "cbt",
        "cbz",
        "cbr",
        "dgc",
        "dmg",
        "ear",
        "gz",
        "gzip",
        "ha",
        "ice",
        "jar",
        "kgb",
        "lzh",
        "lz",
        "lzma",
        "lzo",
        "pak",
        "partimg",
        "pea",
        "pet",
        "pk3",
        "pk4",
        "rar",
        "rpm",
        "rz",
        "s7z",
        "sda",
        "sea",
        "sen",
        "sfark",
        "sfx",
        "shar",
        "sit",
        "sitx",
        "sqx",
        "tar",
        "tar.bz2",
        "tar.gz",
        "tar.xz",
        "tgz",
        "tlz",
        "txz",
        "udf",
        "utz",
        "uu",
        "uue",
        "war",
        "wim",
        "xar",
        "xp3",
        "xz",
        "z",
        "zip",
        "zipx",
        "zoo",
        "zpaq",
    )

    @JvmField
    val audioExtensions = arrayOf(
        "mp3",
        "m4a",
        "wav",
        "flac",
        "midi",
        "wma",
        "aac",
        "ogg",
        "opus",
        "aiff",
    )

    @JvmField
    val videoExtensions = arrayOf(
        "mp4",
        "avi",
        "mov",
        "mkv",
        "flv",
        "webm",
        "vob",
        "ogv",
        "gif",
        "gifv",
        "mng",
        "wmv",
        "yuv",
        "amv",
    )

    @JvmField
    val imageExtensions = arrayOf(
        "jpg",
        "jpeg",
        "webp",
        "bmp",
        "png",
        "raw",
        "psd",
    )

    @JvmField
    val windowsExtensions = arrayOf(
        "exe",
        "msi",
        "ini",
    )

    @JvmField
    val fontExtensions = arrayOf(
        "ttf",
        "otf",
    )


    val knownExtensions : Set<String> = (imageExtensions + videoExtensions + audioExtensions + officeExtensions + archiveExtensions + apkExtensions + fontExtensions + windowsExtensions).toSet()


    @JvmField
    val docFileType = arrayOf("doc", "docx")

    @JvmField
    val excelFileType = arrayOf("xls", "xlsx")

    @JvmField
    val pptFileType = arrayOf("ppt", "pptx")

    @JvmField
    val fontFileType = arrayOf("ttf", "otf")

    @JvmField
    val vectorFileType =
        arrayOf("svg", "ai", "eps", "pdf", "dxf", "wmf", "emf", "cdr", "odg", "swf")

    @JvmField
    val archiveFileType = arrayOf(
        "zip",
        "7z",
        "tar",
        "jar",
        "gz",
        "xz",
        "obb",
        "rar",
        "iso",
        "bz2",
        "tgz",
        "tbz2",
        "lz",
        "lzma",
    )

    @JvmField
    val videoFileType = arrayOf(
        "mp4",
        "mov",
        "avi",
        "mkv",
        "wmv",
        "m4v",
        "3gp",
        "webm",
        "flv",
        "mpeg",
        "mpg",
        "ogv",
        "mxf",
        "vob",
        "ts"
    )

    @JvmField
    val codeFileType = arrayOf(
        javaFileType,
        "xml",
        "py",
        "css",
        kotlinFileType,
        "cs",
        "xml",
        jsonFileType,
        "html",
        "js",
        "ts",
        "php",
        "rb",
        "pl",
        "sh",
        "cpp",
        "c",
        "h",
        "swift",
        "go",
        "rs",
        "scala",
        "sql",
        "r",
        "md",
        "ini",
        "yaml",
        "yml"
    )

    @JvmField
    val editableFileType = arrayOf(
        "txt",
        "text",
        "log",
        "dsc",
        "apt",
        "rtf",
        "rtx",
        "md",
        "csv",
        "tsv",
        "ini",
        "conf",
        "cfg",
        "nfo",
        "json",
        "xml"
    )

    @JvmField
    val imageFileType = arrayOf(
        "png",
        "jpeg",
        "jpg",
        "heic",
        "tiff",
        "gif",
        "webp",
        svgFileType,
        "bmp",
        "raw",
        "cr2",
        "nef",
        "orf",
        "sr2",
        "psd",
        "ai",
        "eps"
    )

    @JvmField
    val audioFileType = arrayOf(
        "mp3",
        "4mp",
        "aup",
        "ogg",
        "3ga",
        "m4b",
        "wav",
        "acc",
        "m4a",
        "flac",
        "aac",
        "wma",
        "aiff",
        "amr",
        "midi",
        "mid",
        "opus"
    )

    @JvmField
    val apkBundleFileType = arrayOf("apks", "xapk", "apkm")

}