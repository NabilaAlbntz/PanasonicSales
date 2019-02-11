package pogumedia.panasonic.sales.helper.image

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.widget.Toast
import pogumedia.panasonic.sales.R
import pogumedia.panasonic.sales.helper.util.Constants
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

fun advancedConfig(uCrop: UCrop, activity: Activity): UCrop {
    val options = UCrop.Options()
    options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
    options.setCompressionQuality(60)
    options.withMaxResultSize(1080, 720)
    options.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
    options.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
    options.setActiveWidgetColor(ContextCompat.getColor(activity, R.color.colorAccent))
    options.setMaxBitmapSize(650)

    return uCrop.withOptions(options)
}


fun handleCrop(result: Intent): File? {

    val resultUri = UCrop.getOutput(result)
    var file: File? = null
    if (resultUri != null) {
        try {
            file = saveCroppedImage(resultUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    return file
}

@Throws(IOException::class)
fun saveCroppedImage(source: Uri): File {

    val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.DIRECTORY_NAME)
    if (!mediaStorageDir.exists()) {
        mediaStorageDir.mkdir()
    }

    val filename = source.toString().substring(source.toString().lastIndexOf("/") + 1)

    val newMediaFile = File(mediaStorageDir.path + File.separator + filename)


    val inStream = FileInputStream(File(source.path))
    val outStream = FileOutputStream(newMediaFile)
    val inChannel = inStream.channel
    val outChannel = outStream.channel
    inChannel.transferTo(0, inChannel.size(), outChannel)
    inStream.close()
    outStream.close()

    return newMediaFile

}
