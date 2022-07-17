# Arenillas Turístico
Aplicación turística como **Trabajo de Titulación** que trata sobre la promoción de los puntos de interés del cantón Arenillas (Ecuador), que integra realidad aumentada y geolocalización.

La aplicación consta de 5 partes tales como:

1. Obtiene la lista de los sitios turísticos clasificados por categorías.
2. Mapa donde se visualiza todos puntos por medio de sus datos geográficos.
3. Una galería donde se recopila todas las imagenes de los sitios turísticos.
4. Se visulizan los marcadores de los lugares por medio de la realidad aumentada a través de la cámara del dispositivo.
5. Formulario de contacto con el desarrollador.

El proyecto esta construido en [Kotlin][1], las vistas se vinculan con [Data Binding][2], además, se usa [Realtime Database][3] como fuente de datos remota de los sitios turísticos y [Room Database][4] como datos locales que se sincroniza con los datos remotos durante la inicialización de la aplicación para ofrecer un servicio offline.
<br>
La geolocalización de los puntos por medio de los mapas se usa [Mapbox SDK][5] y la experiencia de la realidad aumentada con [Wikitude][6] usando la API en JavaScript.
<br>
La descarga de las imágenes se realizan por medio de [Glide][7], tambien se emplea [Android Image Slider][8] para incluir un carrusel de imágenes en los detalles de los puntos de intéres y se usa [PhotoView][9] para ofrecer una características de zoom.
<br>
La administración de los permisos de Android de GPS y cámara se realizan por medio de [Kpermissions][10].
<br>
Los datos del formulario se guardan en Firebase usando [Firestore][11]
<br>
Todo el proyecto emplea el pratron de arquitectura MVVM y componentes de [Android Jetpack][12] para navegación, viewmodels, livedata, entre otras.

# Capturas de pantalla

![alt text](https://i.ibb.co/6y1dF72/screen-shot-app-arenillas.png)

A continuacion se presentan las diferentes tecnologías y herramientas utilizadas en esta aplicación:

## Componentes de Android Jetpack:
* [ViewModel][13]
* [LiveData][14]
* [Navigation][15]
* [Data Binding][2]
* [Room Database][4]
* [RecyclerView][17]

## Productos de Firebase:
* [Realtime Databse][3]
* [Cloud Firestore][11]

## Realidad Aumentada:
* [Wikitude SDK][6]

## Mapas:
* [Mapbox SDK][5]

## Injección de dependencias:
* [Manual Dependency Injection][16]

## Programación Asíncrona:
* [Kotlin Coroutines][9]

## Otras librerías:
* [Glide][7]
* [Android Image Slider][8]
* [PhotoView][9]
* [Kpermissions][10]

---

# Consideraciones

Si se clona el proyecto y quieres ejecutar la app en tu entorno, debes agregar el archivo **google-services.json** configurado con tu proyecto en Firebase, para más información visita la documentación oficial en [como agregar Firebase a tu proyecto en Android][15].

El SDK de Wikitude es de pago, pero ofrece una APIKEY por 45 días gratis y una de 1 año solicitando la versión educativa, para más información visita [Wikitude Academy][19].
En la ruta de este archivo de recurso [api_key_wikitude.xml](app/src/main/res/values/api_key_wikitude.xml) debe colocar su APIKEY de Wikitude.

El SDK de Mapbox se basa en dos APIKEY, el token secreto (más información en: [creando un token de acceso secreto][20]) y token público que deben colocar en las siguientes direcciones:
* En **gradle.properties** debe colocar su token secreto en la variable "MAPBOX_DOWNLOADS_TOKEN".
* En la ruta de este archivo de recurso [api_key_mapbox.xml](app/src/main/res/values/api_key_mapbox.xml) debe colocar su APIKEY pública.

[1]: https://kotlinlang.org/
[2]: https://developer.android.com/topic/libraries/data-binding
[3]: https://firebase.google.com/docs/database
[4]: https://developer.android.com/training/data-storage/room
[5]: https://www.mapbox.com/maps
[6]: https://www.wikitude.com/products/wikitude-sdk/
[7]: https://github.com/bumptech/glide
[8]: https://github.com/smarteist/Android-Image-Slider
[9]: https://github.com/Baseflow/PhotoView
[10]: https://github.com/fondesa/kpermissions
[11]: https://firebase.google.com/docs/firestore
[12]: https://developer.android.com/jetpack
[13]: https://developer.android.com/topic/libraries/architecture/viewmodel
[14]: https://developer.android.com/topic/libraries/architecture/livedata
[15]: https://developer.android.com/guide/navigation
[16]: https://developer.android.com/training/dependency-injection/manual
[17]: https://developer.android.com/guide/topics/ui/layout/recyclerview
[18]: https://firebase.google.com/docs/android/setup
[19]: https://www.wikitude.com/wikitude-academy/
[20]: https://docs.mapbox.com/accounts/guides/tokens/

