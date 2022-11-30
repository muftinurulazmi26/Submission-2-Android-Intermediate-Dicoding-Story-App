package dev.mufadev.storyapp.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class Helper {

    companion object {
        val timeStamp: String = SimpleDateFormat(
            "dd-MMM-yyyy",
            Locale.US
        ).format(System.currentTimeMillis())

        fun createCustomTempFile(context: Context): File {
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(timeStamp, ".jpg", storageDir)
        }

        fun uriToFile(selectedImg: Uri, context: Context): File {
            val contentResolver: ContentResolver = context.contentResolver
            val myFile = createCustomTempFile(context)

            val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
            val outputStream: OutputStream = FileOutputStream(myFile)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()

            return myFile
        }

        fun reduceFileImage(file: File): File {
            val bitmap = BitmapFactory.decodeFile(file.path)

            var compressQuality = 100
            var streamLength: Int

            do {
                val bmpStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                compressQuality -= 5
            } while (streamLength > 1000000)

            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

            return file
        }
        fun vectorToBitmap(context: Context, drawableId: Int): BitmapDescriptor {
            val drawable = ContextCompat.getDrawable(context, drawableId)
            val bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }
}