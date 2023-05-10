package com.example.customgallery.classes

import android.Manifest
import android.content.Intent
import com.example.customgallery.adapters.GalleryPicturesAdapter
import com.example.customgallery.models.GalleryViewModel
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.customgallery.R
import com.example.customgallery.databinding.ActivityMultiGalleryUiBinding

class MultiCustomGalleryUI : AppCompatActivity() {

    private lateinit var binding: ActivityMultiGalleryUiBinding
    private val adapter by lazy {
        GalleryPicturesAdapter(pictures, SelectionActivity.limit)
    }

    companion object {
        lateinit var list: List<GalleryPicture>
    }

    private val galleryViewModel: GalleryViewModel by viewModels()

    private val pictures by lazy {
        ArrayList<GalleryPicture>(galleryViewModel.getGallerySize(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutInflater = LayoutInflater.from(this)
        binding = ActivityMultiGalleryUiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        requestReadStoragePermission()
    }

    private fun requestReadStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (
                Permission_Utility.checkPermission(this@MultiCustomGalleryUI)) {
                init()
            } else {
                Permission_Utility.anaylyze(this@MultiCustomGalleryUI)
            }
        } else {
            val readStorage = Manifest.permission.READ_EXTERNAL_STORAGE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                    this,
                    readStorage
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(readStorage), 3)
            } else init()

        }

    }

    private fun init() {
        // galleryViewModel = ViewModelProviders.of(this)[com.example.customgallery.models.GalleryViewModel::class.java] /** @deprecated */
        updateToolbar(0)
        val layoutManager = GridLayoutManager(this, 4)
        val pageSize = 20
        binding.rv.layoutManager = layoutManager
        binding.rv.addItemDecoration(SpaceItemDecoration(8))
        binding.rv.adapter = adapter

        adapter.setOnClickListener { galleryPicture ->
            showToast(galleryPicture.path)
        }

        adapter.setAfterSelectionListener {
            updateToolbar(getSelectedItemsCount())
        }

        binding.rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (layoutManager.findLastVisibleItemPosition() == pictures.lastIndex) {
                    loadPictures(pageSize)
                }
            }
        })

        binding.toolbar.tvDone.setOnClickListener {
            getSelectedPath()
            startActivity(Intent(this@MultiCustomGalleryUI, FinalActivity::class.java))

        }

        binding.toolbar.ivBack.setOnClickListener {
            onBackPressed()
        }
        loadPictures(pageSize)
    }


    private fun getSelectedItemsCount() = adapter.getSelectedItems().size

    private fun getSelectedPath() {
        list = adapter.getSelectedItems()
    }

    private fun loadPictures(pageSize: Int) {
        galleryViewModel.getImagesFromGallery(this, pageSize) {
            if (it.isNotEmpty()) {
                pictures.addAll(it)
                adapter.notifyItemRangeInserted(pictures.size, it.size)
            }
            Log.i("GalleryListSize", "${pictures.size}")
        }
    }

    private fun updateToolbar(selectedItems: Int) {
        val data = if (selectedItems == 0) {
            binding.toolbar.tvDone.visibility = View.GONE
            getString(R.string.txt_gallery)
        } else {
            binding.toolbar.tvDone.visibility = View.VISIBLE
            "$selectedItems/${adapter.getSelectionLimit()}"
        }
        binding.toolbar.tvTitle.text = data
    }

    override fun onBackPressed() {
        if (adapter.removedSelection()) {
            super.onBackPressed()
            //updateToolbar(0)
        } else {
            super.onBackPressed()
        }
    }

    private fun showToast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            init()
        else {
            showToast("Permission Required to Fetch Gallery.")
            super.onBackPressed()
        }
    }

}