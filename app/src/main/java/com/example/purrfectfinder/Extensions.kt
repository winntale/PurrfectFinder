package com.example.purrfectfinder

import android.content.Context
import android.net.Uri
import okio.IOException
import kotlin.jvm.Throws


@Throws(IOException::class)
fun Uri.uriToByteArray(context: Context): ByteArray? {
    return try {
        context.contentResolver.openInputStream(this)?.use { inputStream ->
            inputStream.readBytes() // читает весь поток данных в массив байтов
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}