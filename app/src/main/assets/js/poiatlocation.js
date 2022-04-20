/* Implementation of AR-Experience (aka "World"). */
let World = {
  /* True once data was fetched. */
  initiallyLoadedData: false,

  /* pOI-Marker asset. */
  markerDrawableIdle: null,
  markerDrawableSelected: null,

  /* List of AR.GeoObjects that are currently shown in the scene / World. */
  placeList: [],

  /* the last selected marker. */
  currentMarker: null,

  /* Updates status message shown in small "i"-button aligned bottom center. */
  updateStatusMessage: function updateStatusMessageFn(message, isWarning) {
    document.getElementById("popupButtonImage").src = isWarning
      ? "assets/warning_icon.png"
      : "assets/info_icon.png";
    document.getElementById("popupButtonTooltip").innerHTML = message;
  },

  /* Actualizaciones de ubicacion recibidos desde nativo binding.architectView.setLocation() */
  locationChanged: function locationChangedFn(
    latitude,
    longitude,
    altitude,
    accuracy
  ) {
    let myLocation = {
      latitude: latitude,
      longitude: longitude,
      altitude: altitude,
      accuracy: accuracy,
    };

    document.getElementById("location").innerText =
      "Latitud: " +
      myLocation.latitude +
      ", Longitud: " +
      myLocation.longitude +
      ", Altitud: " +
      myLocation.altitude +
      ", Precision: " +
      myLocation.accuracy;

    /*
            The custom function World.onLocationChanged checks with the flag World.initiallyLoadedData if the
            function was already called. With the first call of World.onLocationChanged an object that contains geo
            information will be created which will be later used to create a marker using the
            World.loadPoisFromJsonData function.
        */
    if (!World.initiallyLoadedData) {
      //World.loadPlaceFromJsonData(poiData);
      World.initiallyLoadedData = true;
    }
  },

  onError: function onErrorFn(error) {
    alert(error);
  },

  /** Lista de lugares que se reciben de la base de datos **/
  getPlacesFromDataBase: function (place) {
    let placeAr = {
      id: place[47].id,
      nombre: place[47].nombre,
      latitud: place[47].latitud,
      longitud: place[47].longitud,
      altitud: place[47].altitud,
      categoria: place[47].categoria,
    };

    document.getElementById("place").innerText =
      "ID: " +
      placeAr.id +
      ", Nombre: " +
      placeAr.nombre +
      ", Categoria: " +
      placeAr.categoria +
      ", Altitud: " +
      placeAr.altitud;

    let listOfPlaces = [];

    for (let index = 0; index < place.length; index++) {
      let placeAr = {
        id: place[index].id,
        nombre: place[index].nombre,
        latitud: place[index].latitud,
        longitud: place[index].longitud,
        altitud: place[index].altitud,
        categoria: place[index].categoria,
      };
      listOfPlaces.push(placeAr);
    }

    World.loadPlaceFromJsonData(listOfPlaces);
  },

  /* Funcion que se llama para agregar un geopoint en AR. */
  loadPlaceFromJsonData: function loadPlaceFromJsonDataFn(places) {

    /* Lista vacia de lugares en la variable miembro de World. */
    World.placeList = [];

    /* Marcador que se usara en el punto geolocalizado */
    World.markerDrawableIdle = new AR.ImageResource("assets/marker_idle.png", {
      onError: World.onError,
    });

    /* Se recorre la lista de lugares, se los almacena en la variable miembro World */
    for (let index = 0; index < places.length; index++) {
      let place = places[index];
      World.placeList.push(new Marker(place))
    }

    /* Mensaje a modo de feedback para conocer los lugares agregados */
    World.updateStatusMessage(World.placeList.length -1 +" lugares cargados");
  },
};

/* Funcion que recibe los datos del usuario desde el entorno nativo y se los pasa a la funcion miembro de World */
AR.context.onLocationChanged = function (
  latitude,
  longitude,
  altitude,
  accuracy
) {
  World.locationChanged(latitude, longitude, altitude, accuracy);
};
