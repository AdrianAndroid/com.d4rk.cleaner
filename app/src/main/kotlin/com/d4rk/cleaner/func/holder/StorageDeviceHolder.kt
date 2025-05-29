package com.d4rk.cleaner.func.holder

data class StorageDeviceHolder(
    val documentHolder: DocumentHolder,
    val title: String,
    val totalSize: Long,
    val usedSize: Long,
    val type: Int
)
