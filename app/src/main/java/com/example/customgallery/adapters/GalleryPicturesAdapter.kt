package com.example.customgallery.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.customgallery.classes.GalleryPicture
import com.example.customgallery.classes.SelectionActivity
import com.example.customgallery.databinding.MultiGalleryListitemBinding

class GalleryPicturesAdapter(private val list: List<GalleryPicture>) :
    RecyclerView.Adapter<GalleryPicturesAdapter.ItemViewHolder>() {

    init {
        initSelectedIndexList()
    }

    constructor(list: List<GalleryPicture>, selectionLimit: Int) : this(list) {
        setSelectionLimit(selectionLimit)
    }

    private lateinit var onClick: (GalleryPicture) -> Unit
    private lateinit var afterSelectionCompleted: () -> Unit
    private var isSelectionEnabled = false
    private lateinit var selectedIndexList: ArrayList<Int> // only limited items are selectable.
    private var selectionLimit = 0


    private fun initSelectedIndexList() {
        selectedIndexList = ArrayList(selectionLimit)
    }

    private fun setSelectionLimit(selectionLimit: Int) {
        this.selectionLimit = selectionLimit
        removedSelection()
        initSelectedIndexList()
    }

    fun setOnClickListener(onClick: (GalleryPicture) -> Unit) {
        this.onClick = onClick
    }

    fun setAfterSelectionListener(afterSelectionCompleted: () -> Unit) {
        this.afterSelectionCompleted = afterSelectionCompleted
    }

    private fun checkSelection(position: Int) {
        if (isSelectionEnabled) {
            if (getItem(position).isSelected)
                selectedIndexList.add(position)
            else {
                selectedIndexList.remove(position)
                isSelectionEnabled = selectedIndexList.isNotEmpty()
            }
        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        val binding = MultiGalleryListitemBinding
            .inflate(LayoutInflater.from(p0.context), p0, false)
        return ItemViewHolder(binding)
    }

    private fun handleSelection(position: Int, context: Context) {

        val picture = getItem(position)

        picture.isSelected = if (picture.isSelected) {
            false
        } else {
            val selectionCriteriaSuccess = getSelectedItems().size < selectionLimit
            if (!selectionCriteriaSuccess)
                selectionLimitReached(context)

            selectionCriteriaSuccess
        }
    }

    fun getSelectionLimit() = selectionLimit

    private fun selectionLimitReached(context: Context) {
        Toast.makeText(
            context,
            "${getSelectedItems().size}/$selectionLimit selection limit reached.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getItem(position: Int) = list[position]

    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        val picture = list[p1]
        p0.bind(picture)
        p0.itemView.setOnClickListener {
            val position = p0.adapterPosition
            val pictures = getItem(position)

            if (isSelectionEnabled) {
                handleSelection(position, it.context)
                notifyItemChanged(position)
                checkSelection(position)
                afterSelectionCompleted()

            } else
                onClick(pictures)

        }
        p0.itemView.setOnClickListener {
            val position = p0.adapterPosition
            isSelectionEnabled = true
            handleSelection(position, it.context)
            notifyItemChanged(position)
            checkSelection(position)
            afterSelectionCompleted()

            isSelectionEnabled
        }
    }

    override fun getItemCount() = list.size

    fun getSelectedItems() = selectedIndexList.map {
        list[it]
    }

    fun removedSelection(): Boolean {
        return if (isSelectionEnabled) {
            selectedIndexList.forEach {
                list[it].isSelected = false
            }
            isSelectionEnabled = false
            selectedIndexList.clear()
            notifyDataSetChanged()
            true

        } else false
    }

    class ItemViewHolder(private val itemBinding: MultiGalleryListitemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(galleryPicture: GalleryPicture) {
            Glide.with(itemView.context).load(galleryPicture.path).into(itemBinding.ivImg)

            if (SelectionActivity.multiple) {
                itemBinding.vSelected.visibility = View.VISIBLE
                itemBinding.vSelected.isSelected = galleryPicture.isSelected
            } else {
                itemBinding.vSelected.visibility = View.GONE
            }
        }
    }
}