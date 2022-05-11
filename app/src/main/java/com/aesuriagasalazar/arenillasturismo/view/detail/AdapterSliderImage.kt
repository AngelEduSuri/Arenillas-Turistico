package com.aesuriagasalazar.arenillasturismo.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import com.aesuriagasalazar.arenillasturismo.databinding.ItemSliderImageBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class AdapterSliderImage(private val images: List<String>) :
    SliderViewAdapter<AdapterSliderImage.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?) = SliderViewHolder.from(parent)

    override fun onBindViewHolder(viewHolder: SliderViewHolder?, position: Int) {
        viewHolder?.bind(images[position])
    }

    override fun getCount() = images.size

    class SliderViewHolder(private val binding: ItemSliderImageBinding) :
        SliderViewAdapter.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            binding.imageUrl = imageUrl
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup?): SliderViewHolder {
                val layoutInflater = LayoutInflater.from(parent?.context)
                val binding = ItemSliderImageBinding.inflate(layoutInflater, parent, false)
                return SliderViewHolder(binding)
            }
        }
    }
}