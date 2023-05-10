package com.example.customgallery.classes

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.customgallery.databinding.ActivityFinalBinding

class FinalActivity : AppCompatActivity() {
    private var size = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this@FinalActivity)
        val binding = ActivityFinalBinding.inflate(inflater)
        val view = binding.root
        setContentView(view)

        size = MultiCustomGalleryUI.list.size
        if (size >= 2) {
            val path1 = MultiCustomGalleryUI.list[0].path
            val path2 = MultiCustomGalleryUI.list[1].path
            val path3 = MultiCustomGalleryUI.list[2].path
            Glide.with(this@FinalActivity).load(path1).into(binding.image1)
            Glide.with(this@FinalActivity).load(path2).into(binding.image2)
            Glide.with(this@FinalActivity).load(path3).into(binding.image3)
        } else {
            val path = MultiCustomGalleryUI.list[0].path
            Glide.with(this@FinalActivity).load(path).into(binding.image2)
        }
    }
}