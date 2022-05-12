package com.aesuriagasalazar.arenillasturismo.view.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aesuriagasalazar.arenillasturismo.databinding.ItemImageGalleryBinding

class AdapterGallery(private val imageClickListener: ImageClickListener) :
    RecyclerView.Adapter<AdapterGallery.ViewHolder>() {

    var gallery = listOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(gallery[position], imageClickListener)

    override fun getItemCount() = gallery.size

    class ViewHolder private constructor(private val binding: ItemImageGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: String, imageClick: ImageClickListener) {
            binding.image = image
            binding.imageClick = imageClick
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemImageGalleryBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ImageClickListener(private val clickListener: (String) -> Unit) {
    fun onImageClick(image: String) = clickListener(image)
}