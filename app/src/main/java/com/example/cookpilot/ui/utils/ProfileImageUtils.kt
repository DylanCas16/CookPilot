package com.example.cookpilot.ui.utils

import APPWRITE_BUCKET_ID
import APPWRITE_PROJECT_ID
import APPWRITE_PUBLIC_ENDPOINT
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun buildProfileImageUrl(fileId: String?, bucketId: String = APPWRITE_BUCKET_ID): String? {
    if (fileId == null) return null
    val endpoint = APPWRITE_PUBLIC_ENDPOINT
    val projectId = APPWRITE_PROJECT_ID
    return "$endpoint/storage/buckets/$bucketId/files/$fileId/view?project=$projectId"
}

fun Context.createImageUri(): Uri {
    val tempImagesDir = File(cacheDir, "images")
    tempImagesDir.mkdirs()
    val file = File(tempImagesDir, "temp_photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        this,
        "${packageName}.provider",
        file
    )
}