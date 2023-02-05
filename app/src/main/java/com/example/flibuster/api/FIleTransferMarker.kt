package com.example.flibuster.api

enum class FileTransferMarker(val mode: Int) {
    NONE(0),
    DOWNLOAD(1),
    UPLOAD(2),
}