package com.example.flibuster.api

interface SpeedTestListener {

    fun onComplete()

    fun onNext(model: FileTransferModel)

    fun onError(throwable: Throwable)
}