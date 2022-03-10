package com.aesuriagasalazar.arenillasturismo.model

import android.content.res.Resources
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.aesuriagasalazar.arenillasturismo.R
import com.bumptech.glide.Glide
import com.google.firebase.database.core.Context

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




@BindingAdapter("image_load_url")
fun imageUrl(imageView: ImageView, url: String) {
    val circularProgressDrawable = CircularProgressDrawable(imageView.context)
    circularProgressDrawable.strokeWidth = 15f
    circularProgressDrawable.centerRadius = 100f
    circularProgressDrawable.start()

    Glide
        .with(imageView)
        .load(url)
        .centerCrop()
        .placeholder(circularProgressDrawable)
        .error(R.drawable.icon_error_load_image)
        .into(imageView)
}

fun Int.toStringCategory(resource: Resources): String {
    return resource.getString(this)
}