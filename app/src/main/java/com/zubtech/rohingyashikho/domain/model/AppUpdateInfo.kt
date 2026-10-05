package com.zubtech.rohingyashikho.domain.model

data class AppUpdateInfo(
    val message: String = "",
    val updateUrl: String = "",
    val version: Int = 0,
    val showNotification: Boolean = false,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val fontSize: Int = 16,
    val textColor: Long = 0xFF000000,
    val highlightColor: Long = 0x00000000
)
