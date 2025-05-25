package com.d4rk.cleaner.utils.helpers

import java.io.File

/**
 * 文件大小工具类，提供获取文件或文件夹大小的功能
 */
object FileSizeHelper {

    /** 文件大小单位的枚举类，包含单位名称和对应的字节数 */
    enum class SizeUnit(val unit: String, val byteSize: Long) {
        BYTES("B", 1),
        KILOBYTES("KB", 1024),
        MEGABYTES("MB", 1024 * 1024),
        GIGABYTES("GB", 1024 * 1024 * 1024);

        /** 将字节数转换为当前单位表示的大小 */
        fun convert(bytes: Long): Double = bytes.toDouble() / byteSize
    }

    /**
     * 获取文件的大小（字节）
     * @param file 目标文件
     * @return 文件大小（字节），若文件不存在或出错返回 0
     */
    fun getFileSize(file: File): Long {
        return if (file.exists() && file.isFile) {
            file.length()
        } else {
            0L
        }
    }

    /**
     * 递归获取文件夹的总大小（字节）
     * @param dir 目标文件夹
     * @return 文件夹总大小（字节），若文件夹不存在或出错返回 0
     */
    fun getDirectorySize(dir: File): Long {
        if (!dir.exists() || !dir.isDirectory) {
            return 0L
        }

        var totalSize = 0L
        val files = dir.listFiles()
        files?.forEach { file ->
            totalSize += if (file.isDirectory) {
                getDirectorySize(file)
            } else {
                file.length()
            }
        }
        return totalSize
    }

    /**
     * 获取文件或文件夹的大小（自动选择合适的单位）
     * @param file 目标文件或文件夹
     * @return 包含大小数值和单位的字符串，如 "2.5 MB"
     */
    fun getFormattedSize(file: File): String {
        val sizeInBytes = if (file.isDirectory) {
            getDirectorySize(file)
        } else {
            getFileSize(file)
        }

        if (sizeInBytes == 0L) return "0 B"

        val units = listOf(
            SizeUnit.GIGABYTES,
            SizeUnit.MEGABYTES,
            SizeUnit.KILOBYTES,
            SizeUnit.BYTES
        )

        for (unit in units) {
            if (sizeInBytes >= unit.byteSize) {
                return "%.2f %s".format(unit.convert(sizeInBytes), unit.unit)
            }
        }

        return "$sizeInBytes B"
    }
}