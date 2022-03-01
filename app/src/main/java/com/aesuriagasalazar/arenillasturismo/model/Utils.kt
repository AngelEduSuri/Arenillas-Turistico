package com.aesuriagasalazar.arenillasturismo.model

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.aesuriagasalazar.arenillasturismo.R

data class Category(val icon: Int, val title: Int)

object CategoryStatic {
    fun getCategories() = listOf(
        Category(R.drawable.icon_park, R.string.parks),
        Category(R.drawable.icon_food, R.string.food),
        Category(R.drawable.icon_nature, R.string.nature),
        Category(R.drawable.icon_sport, R.string.sports),
        Category(R.drawable.icon_lodging, R.string.lodging),
        Category(R.drawable.icon_entertainment, R.string.entertainment)
    )
}

@BindingAdapter("icon_category")
fun imageResource(imageView: ImageView, res: Int) {
    imageView.setImageResource(res)
}

@BindingAdapter("title_category")
fun textResource(textView: TextView, res: Int) {
    textView.setText(res)
}