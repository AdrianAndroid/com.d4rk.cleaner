package com.d4rk.cleaner.utils.constants.cleaning

/**
 * ✅ 空文件（夹）
 * ✅ 冗余文件
 * ✅ 安装包
 * ✅ 图片
 * ✅ 安装包
 * ✅ 图片
 * ✅ 视频
 * ✅ 音频
 * ✅ 文档
 * ✅ 压缩包
 *
 * ❗️ 新增：
 * 大文件
 * 新文件
 */
object ExtensionsConstants {
    const val GENERIC_EXTENSIONS = "generic_extensions" // 冗余文件
    const val ARCHIVE_EXTENSIONS = "archive_extensions" // 压缩包
    const val APK_EXTENSIONS = "apk_extensions" // 安装包
    const val EMPTY_FOLDERS = "empty_folders" // 空文件夹
    const val IMAGE_EXTENSIONS = "image_extensions" // 图片
    const val AUDIO_EXTENSIONS = "audio_extensions" // 音频
    const val VIDEO_EXTENSIONS = "video_extensions" // 视频
    const val WINDOWS_EXTENSIONS = "windows_extensions" // windows下执行文件
    const val OFFICE_EXTENSIONS = "office_extensions" // 文档
    const val FONT_EXTENSIONS = "font_extensions" // 字体
    const val OTHER_EXTENSIONS = "other_extensions"

    const val BIG_FILE_EXTENSION = "big_file_extension" // 大文件
    const val NEW_FILE_EXTENSION = "new_file_extension" // 新文件
}