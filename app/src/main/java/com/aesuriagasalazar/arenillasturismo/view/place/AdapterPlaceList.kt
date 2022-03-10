package com.aesuriagasalazar.arenillasturismo.view.place

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aesuriagasalazar.arenillasturismo.databinding.ItemPlaceListBinding
import com.aesuriagasalazar.arenillasturismo.model.domain.Place

class AdapterPlaceList(private val placeClickListener: PlaceClickListener): RecyclerView.Adapter<AdapterPlaceList.ViewHolder>() {

    var listPlaces = emptyList<Place>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(listPlaces[position], placeClickListener)

    override fun getItemCount() = listPlaces.size

    class ViewHolder private constructor(private val binding: ItemPlaceListBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Place, placeClick: PlaceClickListener) {
            binding.place = place
            binding.placeClick = placeClick
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val biding = ItemPlaceListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(biding)
            }
        }
    }
}

class PlaceClickListener(private val clickListener: (Place) -> Unit) {
    fun onPlaceClick(place: Place) = clickListener(place)
}