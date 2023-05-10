package com.example.customgallery.classes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.example.customgallery.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    companion object {
        var single = false
        var multiple = false
        var limit = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = LayoutInflater.from(this@SelectionActivity)
        val binding = ActivitySelectionBinding.inflate(inflater)
        val view = binding.root
        setContentView(view)


        binding.single.setOnClickListener {
            single = true
            multiple = false
            limit = 1
            startActivity(Intent(this@SelectionActivity, MultiCustomGalleryUI::class.java))
        }

        binding.multiple.setOnClickListener {
            single = false
            multiple = true
            limit = 3
            startActivity(Intent(this@SelectionActivity, MultiCustomGalleryUI::class.java))
        }
    }
}