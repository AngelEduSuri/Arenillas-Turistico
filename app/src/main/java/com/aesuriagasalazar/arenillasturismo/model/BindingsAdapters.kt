package com.aesuriagasalazar.arenillasturismo.model

import android.content.res.Resources
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.aesuriagasalazar.arenillasturismo.R
import com.aesuriagasalazar.arenillasturismo.model.domain.Place
import com.aesuriagasalazar.arenillasturismo.viewmodel.DataStatus
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager

/** Data class para representar las categorias de los lugares
 * @constructor Crea un objeto category
 * @param icon Valor entero del recurso dibujable
 * @param title El nombre de la categoria
 */
data class Category(val icon: Int, val title: Int)

/** Singleton que devuelve la lista de las categorias **/
object CategoryStatic {
    fun getCategories() = listOf(
        Category(R.drawable.icon_park, R.string.parks),
        Category(R.drawable.icon_history, R.string.history),
        Category(R.drawable.icon_nature, R.string.nature),
        Category(R.drawable.icon_sport, R.string.sports),
        Category(R.drawable.icon_lodging, R.string.lodging),
        Category(R.drawable.icon_entertainment, R.string.entertainment)
    )
}

/** Funciones databinding para vincular las vistas con funciones **/

@BindingAdapter("text_message")
fun textMessage(textView: TextView, dataStatus: DataStatus?) {
    dataStatus?.let {
        val context = textView.context
        when (dataStatus) {
            DataStatus.UPDATING -> textView.text = context.getString(R.string.updating)
            DataStatus.DOWNLOADING -> textView.text = context.getString(R.string.downloading)
            DataStatus.ERROR -> textView.text = context.getString(R.string.error)
            DataStatus.NO_NETWORK -> textView.text = context.getString(R.string.network)
            DataStatus.LOCAL -> textView.text = context.getString(R.string.local)
        }
    }
}

@BindingAdapter("icon_category")
fun imageResource(imageView: ImageView, res: Int) {
    imageView.setImageResource(res)
}

@BindingAdapter("title_category")
fun textResource(textView: TextView, textId: Int) {
    val res = textView.resources

    when (val title = res.getString(textId)) {
        res.getString(R.string.parks) -> textView.text = title.plus("s")
        res.getString(R.string.history) -> textView.text =
            res.getString(R.string.title_places).plus(" ${title}s")
        else -> textView.text = title
    }
}

@BindingAdapter("image_load_url")
fun imageUrl(imageView: ImageView, url: String) {
    Glide
        .with(imageView.context)
        .load(url)
        .centerCrop()
        .apply(
            RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.broken_image)
        )
        .into(imageView)
}

@BindingAdapter("image_load_slider")
fun imageUrlSlider(imageView: ImageView, imageUrl: String) {
    Glide
        .with(imageView.context)
        .load(imageUrl.toUri())
        .fitCenter()
        .apply(
            RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.broken_image)
        )
        .into(imageView)
}

@BindingAdapter("load_init_camera")
fun initCameraMapbox(mapView: MapView, place: Place) {
    val point = Point.fromLngLat(place.longitud, place.latitud, place.altitud.toDouble())
    mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
        val annotationApi = mapView.annotations
        val pointAnnotationManager = annotationApi.createCircleAnnotationManager()
        val pointAnnotationOptions = CircleAnnotationOptions()
            .withPoint(point)
            .withCircleRadius(18.0)
            .withCircleColor("#ee4e8b")
            .withCircleStrokeWidth(8.0)
            .withCircleStrokeColor("#ffffff")
        pointAnnotationManager.create(pointAnnotationOptions)

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(17.5)
                .build()
        )
    }
}

@BindingAdapter("lock_camera_area")
fun lockCameraMap(mapView: MapView, cameraBounds: CameraBoundsOptions) {
    mapView.getMapboxMap().setBounds(cameraBounds)
}

@BindingAdapter("load_icon_location")
fun loadIconButtonLocation(floatingButton: FloatingActionButton, value: Boolean) {
    if (value) {
        floatingButton.setImageResource(R.drawable.icon_my_location_24)
    } else {
        floatingButton.setImageResource(R.drawable.icon_location_disable_24)
    }
}

@BindingAdapter("text_underline")
fun textUnderLine(textView: TextView, text: String) {
    SpannableString(text).apply {
        setSpan(UnderlineSpan(), 0, length, 0)
        textView.text = this
    }
}

/** Funcion de extension para obtener un String desde la id del recurso **/
fun Int.toStringCategory(resource: Resources): String {
    return resource.getString(this)
}

/** Singleton que devuelve el icono de la categoria al que pertenece el lugar en el mapa **/
object IconMap {
    fun getIconMap(place: Place, resource: Resources): Int {
        return when (place.categoria) {
            R.string.parks.toStringCategory(resource).lowercase() -> {
                R.drawable.location_park
            }
            R.string.history.toStringCategory(resource).lowercase() -> {
                R.drawable.location_history
            }
            R.string.nature.toStringCategory(resource).lowercase() -> {
                R.drawable.location_nature
            }
            R.string.sports.toStringCategory(resource).lowercase() -> {
                R.drawable.location_sports
            }
            R.string.lodging.toStringCategory(resource).lowercase() -> {
                R.drawable.location_hotel
            }
            R.string.entertainment.toStringCategory(resource).lowercase() -> {
                R.drawable.location_entertaiment
            }
            else -> {
                R.drawable.red_marker
            }
        }
    }
}
