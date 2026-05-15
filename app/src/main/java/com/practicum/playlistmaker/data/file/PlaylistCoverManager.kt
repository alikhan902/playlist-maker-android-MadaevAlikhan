package com.practicum.playlistmaker.data.file

import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

class PlaylistCoverManager(private val context: Context) {

    private val coversDirectory: File
        get() = File(context.filesDir, "playlist_covers").apply {
            if (!exists()) {
                mkdirs()
            }
        }


    fun saveCoverImage(sourceUri: Uri?): String? {
        if (sourceUri == null) return null

        return try {
            val inputStream = context.contentResolver.openInputStream(sourceUri)
            if (inputStream == null) {
                return null
            }

            val fileName = "${UUID.randomUUID()}.jpg"
            val outputFile = File(coversDirectory, fileName)

            inputStream.use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteCoverImage(localPath: String?) {
        if (localPath == null) return

        try {
            val file = File(localPath)
            if (file.exists() && file.absolutePath.startsWith(coversDirectory.absolutePath)) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isLocalPath(path: String?): Boolean {
        if (path == null) return false
        return try {
            File(path).absolutePath.startsWith(coversDirectory.absolutePath)
        } catch (e: Exception) {
            false
        }
    }
}
