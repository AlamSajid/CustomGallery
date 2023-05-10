package com.example.customgallery.classes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.customgallery.R
import com.example.customgallery.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {
    var binding: ActivityPermissionBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this)
        binding = ActivityPermissionBinding.inflate(inflater)
        val view = binding!!.root
        setContentView(view)
        Glide.with(this).load(R.drawable.permission_deny).into(binding!!.image)
        binding!!.backBtn.setOnClickListener { onBackPressed() }
        binding!!.btn.setOnClickListener {
            if (binding!!.btn.text.toString().contentEquals("Setting Allow")) {
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri =
                    Uri.fromParts("package", getPackageName(), null)
                intent.data = uri
                startActivity(intent)
                finish()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@PermissionActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        this@PermissionActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    binding!!.btn.setText("Setting Allow")
                }
            }
        }
    }
}