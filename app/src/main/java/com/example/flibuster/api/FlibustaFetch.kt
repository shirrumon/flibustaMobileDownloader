package com.example.flibuster.api

import android.app.Activity
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import okhttp3.*
import okio.BufferedSink
import okio.buffer
import okio.sink
import org.jsoup.Jsoup
import java.io.*

class FlibustaFetch {
    private val baseUrl = "https://flibusta.site"
    private val client = OkHttpClient()
    private val pathToDownloads =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    fun findBook(bookName: String): MutableList<Map<String, String>> {
        val result = mutableListOf<Map<String, String>>()

        val doc = Jsoup.connect(
            this.baseUrl + "/booksearch?ask=" + bookName.replace(
                "\\s".toRegex(),
                "+"
            )
        ).get()    // <1>
        doc.select("div#main").select("li").select("a")
            .forEach {
                if (it.attr("href").contains("/b/", ignoreCase = true)) {
                    val bookArticle = Jsoup.parse(it.html()).text()
                    val bookLink = it.attr("href")
                    result.add(mapOf(bookArticle to bookLink))
                }
            }

        return result
    }

    fun downloadFb2(bookId: String, activity: Activity) {
        val request = Request.Builder()
            .url(baseUrl + bookId + "/fb2")
            .build()

        Log.d("target url", baseUrl + bookId + "/fb2")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    writeFile(
                        bookId.filter { it.isDigit() },
                        "fb2.zip",
                        activity,
                        response.body!!
                    )
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun writeFile(
        fileName: String,
        extension: String,
        activity: Activity,
        body: ResponseBody
    ) {
//        val dir = File(activity.filesDir, "external_files")
//        if (!dir.exists()) {
//            dir.mkdir()
//        }
//        val filename: String = "$fileName.$extension" //bookId.filter { it.isDigit() }
//        val downloadedFile = File(dir, filename)
//        downloadedFile.createNewFile()

        var dir: File? = null
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/Books")
                    .toString()
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/Books" )
        }

        // Make sure the path directory exists.

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            val success = dir.mkdirs()
            if (!success) {
                dir = null
            }
        } else {
            val filename = "$fileName.$extension"
            val downloadedFile = File(dir, filename)
            downloadedFile.createNewFile()

            val inputStream = body.byteStream()
            val fileReader = ByteArray(4096)
            var sizeOfDownloaded = 0
            val fos: OutputStream = FileOutputStream(downloadedFile)

            do {
                val read = inputStream.read(fileReader)
                if(read != -1) {
                    fos.write(fileReader, 0, read)
                    sizeOfDownloaded += read
                }
            } while (read != -1)

            fos.flush()
            fos.close()

            //FileProvider.getUriForFile(activity, "com.example.fileprovider", downloadedFile)

//            val sink: BufferedSink = downloadedFile.sink().buffer()
//            sink.writeAll(body.source())
//            sink.close()
        }
    }
}
