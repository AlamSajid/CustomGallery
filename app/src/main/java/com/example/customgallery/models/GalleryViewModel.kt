package com.example.customgallery.models

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import com.example.customgallery.classes.GalleryPicture
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.LinkedList


class GalleryViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private var startingRow = 0
    private var rowsToLoad = 0
    private var allLoaded = false
    private lateinit var mContext:Context

    fun getImagesFromGallery(
        context: Context,
        pageSize: Int,
        list: (List<GalleryPicture>) -> Unit
    ) {
        this.mContext=context
        compositeDisposable.add(
            Single.fromCallable {
                fetchGalleryImages(context, pageSize)
            }.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    list(it)
                }, {
                    it.printStackTrace()
                })
        )
    }

    fun getGallerySize(context: Context): Int {
        val cursor = getGalleryCursor(context)
        val rows = cursor!!.count
        cursor.close()
        return rows
    }

    private fun fetchGalleryImages(context: Context, rowsPerLoad: Int): List<GalleryPicture> {
        val galleryImageUrls = LinkedList<GalleryPicture>()
        val cursor = getGalleryCursor(context)

        if (cursor != null && !allLoaded) {
            val totalRows = cursor.count

            allLoaded = rowsToLoad == totalRows
            if (rowsToLoad < rowsPerLoad) {
                rowsToLoad = rowsPerLoad
            }

            for (i in startingRow until rowsToLoad) {
                cursor.moveToPosition(i)
                val dataColumnIndex =
                    cursor.getColumnIndex(MediaStore.MediaColumns._ID) //get column index
                val uri = getImageUri(cursor.getString(dataColumnIndex))
                val path = getRealPathFromURI(uri)
                val mimeTypeInfo =
                    getMimeType(path)
                if (mimeTypeInfo.contentEquals("image/jpg") || mimeTypeInfo.contentEquals("image/jpeg")
                    || mimeTypeInfo.contentEquals("image/png")
                ) {
                    galleryImageUrls.add(GalleryPicture(getImageUri(cursor.getString(dataColumnIndex)).toString())) //get Image path from column index
                }
            }
            Log.i("TotalGallerySize", "$totalRows")
            Log.i("GalleryStart", "$startingRow")
            Log.i("GalleryEnd", "$rowsToLoad")

            startingRow = rowsToLoad

            if (rowsPerLoad > totalRows || rowsToLoad >= totalRows)
                rowsToLoad = totalRows
            else {
                if (totalRows - rowsToLoad <= rowsPerLoad)
                    rowsToLoad = totalRows
                else
                    rowsToLoad += rowsPerLoad
            }

            cursor.close()
            Log.i("PartialGallerySize", " ${galleryImageUrls.size}")
        }
        return galleryImageUrls
    }

    private fun getGalleryCursor(context: Context): Cursor? {
        val externalUri = Images.Media.EXTERNAL_CONTENT_URI
        val columns = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATE_MODIFIED,
        )
        val orderBy = MediaStore.MediaColumns.DATE_MODIFIED //order data by modified
        return context.contentResolver
            .query(
                externalUri,
                columns,
                null,
                null,
                "$orderBy DESC"
            )//get all data in Cursor by sorting in DESC order
    }

    private fun getImageUri(path: String) = ContentUris.withAppendedId(
        Images.Media.EXTERNAL_CONTENT_URI,
        path.toLong()
    )

    override fun onCleared() {
        compositeDisposable.clear()
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val result: String?
        val cursor: Cursor = mContext.contentResolver.query(contentURI, null, null, null, null)!!
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(Images.ImageColumns.DATA)
        result = cursor.getString(idx)
        cursor.close()
        return result
    }
}