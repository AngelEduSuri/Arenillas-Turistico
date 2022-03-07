package com.aesuriagasalazar.arenillasturismo.view.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aesuriagasalazar.arenillasturismo.databinding.ItemCategoryListBinding
import com.aesuriagasalazar.arenillasturismo.model.Category

class AdapterCategoryList(
    private val listCategory: List<Category>,
    private val categoryClickListener: CategoryClickListener
): RecyclerView.Adapter<AdapterCategoryList.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(listCategory[position], categoryClickListener)

    override fun getItemCount() = listCategory.size

    class ViewHolder(private val binding: ItemCategoryListBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, categoryClickListener: CategoryClickListener) {
            binding.category = category
            binding.executePendingBindings()
            binding.categoryClick = categoryClickListener
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCategoryListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class CategoryClickListener(val clickListener:(Int) -> Unit) {
    fun onCategoryClick(category: Int) = clickListener(category)
}