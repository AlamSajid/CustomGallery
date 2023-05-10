package com.example.customgallery.classes

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permission_Utility {
    companion object {
        private const val MY_PERMISSIONS_REQUEST = 123
        var count = 0
        var TAG: String = Permission_Utility::class.java.simpleName
        private var permissions: Array<String>? = null

        init {
            permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }


        private var rejected = ArrayList<String>()

        fun checkPermission(context: Context?): Boolean {
            rejected.clear()
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (permission in permissions!!) {
                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            permission
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        Log.d(TAG, "checkPermission: rationale false")
                        if (!rejected.contains(permission)) rejected.add(permission)
                    }
                }
                if (rejected.size > 0) {
                    var perm: Array<String?>? = arrayOfNulls(rejected.size)
                    perm = rejected.toArray(perm)
                    count++
                    ActivityCompat.requestPermissions(
                        (context as Activity?)!!,
                        perm,
                        MY_PERMISSIONS_REQUEST
                    )
                    false
                } else true
            } else true
        }

        fun anaylyze(context: Context) {
            if (count > 2) (context as Activity).startActivityForResult(
                Intent(
                    context,
                    PermissionActivity::class.java
                ), 111
            )
        }
    }

}