package com.aesuriagasalazar.arenillasturismo.model

import android.content.res.Resources
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView

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

    Glide
        .with(imageView.context)
        .load(url)
        .centerCrop()
        .apply(RequestOptions()
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.broken_image))
        .into(imageView)
}

fun Int.toStringCategory(resource: Resources): String {
    return resource.getString(this)
}

@BindingAdapter("image_load_slider")
fun imageUrlSlider(imageView: ImageView, imageUrl: String) {
    Glide
        .with(imageView.context)
        .load(imageUrl.toUri())
        .fitCenter()
        .apply(RequestOptions()
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.broken_image))
        .into(imageView)
}

@BindingAdapter("load_init_camera")
fun initCameraMapbox(mapView: MapView, place: Place) {
    mapView.getMapboxMap().setCamera(
        CameraOptions.Builder()
            .center(Point.fromLngLat(place.longitud, place.latitud, place.altitud.toDouble()))
            .zoom(17.5)
            .build()
    )
}
