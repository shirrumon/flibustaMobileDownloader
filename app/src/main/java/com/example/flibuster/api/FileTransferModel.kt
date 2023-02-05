package com.example.flibuster.api

data class FileTransferModel(
    val totalBytesRead: Long,
    val contentLength: Long,
    val isDone: Boolean,
    val startTimeMillis: Long,
    val fileTransferMarker: FileTransferMarker
)