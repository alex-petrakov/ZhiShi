package me.alex.pet.apps.zhishi.data.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenshotProvider @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun shareScreenshot(bytes: ByteArray): Uri? = withContext(Dispatchers.IO) {
        // TODO: (4) explicitly report any errors
        return@withContext try {
            val screenshotsCacheDir = File("${context.cacheDir}/screenshots")
            if (!screenshotsCacheDir.exists() && !screenshotsCacheDir.mkdirs()) {
                null
            } else {
                // TODO: (5) manage space usage
                val screenshotFile = File.createTempFile("Screenshot", ".jpeg", screenshotsCacheDir)
                screenshotFile.outputStream().use { outputStream ->
                    bytes.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                FileProvider.getUriForFile(
                    context,
                    "me.alex.pet.apps.zhishi.screenshotprovider",
                    screenshotFile
                )
            }
        } catch (e: IOException) {
            null
        } catch (e: SecurityException) {
            null
        } catch (e: FileNotFoundException) {
            null
        }
    }

    private fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }
}