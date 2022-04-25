/* Implementacion de la experiencia en Realidad Aumentada (aka "World"). */
let World = {
  /* Sera true cuando se los datos esten listos. */
  initiallyLoadedData: false,

  /* Asses de los marcadores para los lugares. */
  markerDrawableIdle: null,
  markerDrawableSelected: null,
  indicatorMarkerDrawable: null,

  /* Lista de AR.GeoObjects que se estan mostrando en la escena (World). */
  placeList: [],

  /* El ultimo marcado que esta seleccionado. */
  currentMarker: null,

  /** Contador para actualizar la ubicacion. */
  locationUpdateCounter: 0,
  /** Actualiza los marcadores con la nueva ubicacion.  */
  updatePlacemarkDistancesEveryXLocationUpdates: 10,

  /* Mensaje de estado que se muestra en el boton "i"- alienado en la parte inferior centro. */
  updateStatusMessage: function updateStatusMessageFn(message, isWarning) {
    document.getElementById("popupButtonImage").src = isWarning
      ? "assets/warning_icon.png"
      : "assets/info_icon.png";
    document.getElementById("popupButtonTooltip").innerHTML = message;
  },

  /* Funcion que recibe las actualizaciones de ubicacion desde el entorno nativo binding.architectView.setLocation(). */
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

    /** Se mapea informacion en la vista architectView html. */
    document.getElementById("location").innerText =
      "Latitud: " +
      myLocation.latitude +
      ", Longitud: " +
      myLocation.longitude +
      ", Altitud: " +
      myLocation.altitude +
      ", Precision: " +
      myLocation.accuracy;

    /** Comprueba si ya estan cargados los datos. */
    if (!World.initiallyLoadedData) {
      World.initiallyLoadedData = true;
    } else if (World.locationUpdateCounter === 0) {
     /** Llama a la funcion que actualiza la distancia de los marcadores periodicamente. */
      World.updateDistanceToUserValues();
    }

    /** Ayuda a actualizar los marcadores cada por cada 10 ubicaciones activas. */
    World.locationUpdateCounter =
      ++World.locationUpdateCounter %
      World.updatePlacemarkDistancesEveryXLocationUpdates;
  },

  /** Funcion para mostrar un error */
  onError: function onErrorFn(error) {
    alert(error);
  },

  /** Lista de lugares que se reciben de la base de datos desde nativo. **/
  getPlacesFromDataBase: function (place) {
    /** Se mapea un lugar en una etiqueta html a modo de prueba. */
    document.getElementById("place").innerText =
      "ID: " +
      place[1].id +
      ", Nombre: " +
      place[1].nombre +
      ", Categoria: " +
      place[1].categoria +
      ", Altitud: " +
      place[1].altitud;

    let listOfPlaces = [];

    /** Se recorre la lista de lugares recibidos en formato Json. */
    for (let index = 0; index < place.length; index++) {
      /** Se mapea la informacion que se necesita en un objeto JavaScript. */
      let placeAr = {
        id: place[index].id,
        nombre: place[index].nombre,
        latitud: place[index].latitud,
        longitud: place[index].longitud,
        altitud: place[index].altitud,
        categoria: place[index].categoria,
        descripcion: place[index].descripcion,
      };
      /** Se almacena los objetos en la lista. */
      listOfPlaces.push(placeAr);
    }

    /** Se pasa esa lista para que se cargue en la escena World. */
    World.loadPlaceFromJsonData(listOfPlaces);
  },

  /* Funcion que se llama para agregar los geopoints en AR. */
  loadPlaceFromJsonData: function loadPlaceFromJsonDataFn(placesAR) {

    PlaceRadar.show();

    /* Lista vacia de lugares en la variable miembro de World. */
    World.placeList = [];

    /* Recurso de imagen que se usara en el punto geolocalizado. */
    World.markerDrawableIdle = new AR.ImageResource("assets/marker_idle.png", {
      onError: World.onError,
    });

    /* Recurso de imagen que se usara en el punto geolocalizado cuando se selecciona */
    World.markerDrawableSelected = new AR.ImageResource("assets/marker_selected.png", {
        onError: World.onError,
    });

    /** Recurso de imagen usado para indicar la direccion del lugar. */
    World.indicatorMarkerDrawable = new AR.ImageResource("assets/indi.png", {
      onError: World.onError,
    });

    /* Se recorre la lista de lugares, se los almacena en la variable miembro World. */
    for (let i = 0; i < placesAR.length; i++) {
      let placeAR = placesAR[i];
      World.placeList.push(new Marker(placeAR));
    }

    /* Actualiza los marcadores en sus ubicaciones. */
    World.updateDistanceToUserValues();

    /* Mensaje a modo de feedback para conocer los lugares cargados en la escena World. */
    World.updateStatusMessage(World.placeList.length - 1 + " lugares cargados");
  },

  /** Funcion que recibe el marcador seleccionado. */
  onMarkerSelected: function onMarkerSelectedFn(marker) {
    /* Se cierra el panel en caso de estar abierto. */
    World.closePanel();

    /** Pasamos el marcador recibido desde la clase Marker como marcador actual. */
    World.currentMarker = marker;

    /** Mostramos el panel de detalles */
    document.getElementById("poiDetailTitle").innerHTML = marker.place.nombre;
    document.getElementById("poiDetailDescription").innerHTML =
      marker.place.descripcion;
      
    /** Se recalcula la distancia en caso de que el marcador devuelva un undefined. */
    if (undefined === marker.distanceToUser) {
      marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();
    }

    /** Se convierte la distancia en metro o kilometro si se requiere. */
    let distanceToUserValue =
      marker.distanceToUser > 999
        ? (marker.distanceToUser / 1000).toFixed(2) + " km"
        : Math.round(marker.distanceToUser) + " m";

    /** Se pasa la distancia convertida al marcador html para el usuario. */
    document.getElementById("poiDetailDistance").innerHTML =
      distanceToUserValue;

    /** Se hace visible el panel. */
    document.getElementById("panelPoiDetail").style.visibility = "visible";
  },

  /* Funcion que recibe el click en la pantalla pero no el objeto AR. */
  onScreenClick: function onScreenClickFn() {
    /** Click en cualquier parte vacia de la pantalla, cierra el panel. */
    World.closePanel();
  },

  /** Funcion que cierra el panel de detalles. */
  closePanel: function closePanelFn() {
    /** Ocultamos el panel. */ 
    document.getElementById("panelPoiDetail").style.visibility = "hidden";
    document.getElementById("panelRange").style.visibility = "hidden";

    if (World.currentMarker != null) {
      /* Se deselecciona el marcador cuando el usuario sale del panel de detalle. */
      World.currentMarker.setDeselected(World.currentMarker);
      World.currentMarker = null;
    }
  },

  /** Devuelve la distancia en metros del marcador de posición con maxdistance. */
  getMaxDistance: function getMaxDistanceFn() {
    /** Ordena los lugares por distancia para que el primer lugar sea el que tenga la distancia máxima. */
    World.placeList.sort(World.sortByDistanceSortingDescending);

    /** Se usa la distancia del usuario con el lugar para determinar la distancia maxima. */
    let maxDistanceMeters = World.placeList[0].distanceToUser;

    /** Devuelve la distancia máxima multiplicada por un valor > 1.0 para que quede algo de espacio 
     * y los pequeños movimientos del usuario no hagan que los lugares lejanos desaparezcan. */
    return maxDistanceMeters * 1.1;
  },

  /** Funcion para ordenar los lugares por distancia. */
  sortByDistanceSorting: function sortByDistanceSortingFn(a, b) {
    return a.distanceToUser - b.distanceToUser;
  },

  /** Funcion para ordenar los lugares por distancia descendente. */
  sortByDistanceSortingDescending: function sortByDistanceSortingDescendingFn(a, b) {
    return b.distanceToUser - a.distanceToUser;
  },

  /** Funcion que permite establecer/actualizar todos los marcadores  */
  updateDistanceToUserValues: function updateDistanceToUserValuesFn() {
    for (let i = 0; i < World.placeList.length; i++) {
      World.placeList[i].distanceToUser =
        World.placeList[i].markerObject.locations[0].distanceToUser();
    }
  },

  /** Funcion para actualizar el rango de visualizacion de los lugares en AR */
  updateRangeValues: function updateRangeValuesFn() {
    /* Get current slider value (0..100);. */
    let slider_value = document.getElementById("panelRangeSlider").value;
    /* Max range relative to the maximum distance of all visible places. */
    let maxRangeMeters = Math.round(World.getMaxDistance() * (slider_value / 100));

    /* Range in meters including metric m/km. */
    let maxRangeValue = (maxRangeMeters > 999) ?
        ((maxRangeMeters / 1000).toFixed(2) + " km") :
        (Math.round(maxRangeMeters) + " m");

    /* Number of places within max-range. */
    let placesInRange = World.getNumberOfVisiblePlacesInRange(maxRangeMeters);

    /* Update UI labels accordingly. */
    document.getElementById("panelRangeValue").innerHTML = maxRangeValue;
    document.getElementById("panelRangePlaces").innerHTML = (placesInRange != 1) ?
        (placesInRange + " Places") : (placesInRange + " Place");
    document.getElementById("panelRangeSliderValue").innerHTML = slider_value;

    World.updateStatusMessage((placesInRange != 1) ?
        (placesInRange + " places loaded") : (placesInRange + " place loaded"));

    /* Update culling distance, so only places within given range are rendered. */
    AR.context.scene.cullingDistance = Math.max(maxRangeMeters, 1);

    /* Update radar's maxDistance so radius of radar is updated too. */
    PlaceRadar.setMaxDistance(Math.max(maxRangeMeters, 1));
  },

  /** Funcion que devuelve el numero de lugares dentro del rango establecido. */
  getNumberOfVisiblePlacesInRange: function getNumberOfVisiblePlacesInRangeFn(maxRangeMeters) {
    /* Sort markers by distance. */
    World.placeList.sort(World.sortByDistanceSorting);

    /* Loop through list and stop once a placemark is out of range ( -> very basic implementation ). */
    for (let i = 0; i < World.placeList.length; i++) {
        if (World.placeList[i].distanceToUser > maxRangeMeters) {
            return i;
        }
    }

    /* In case no placemark is out of range -> all are visible. */
    return World.placeList.length;
  },

  /** */
  handlePanelMovements: function handlePanelMovementsFn() {
    PlaceRadar.updatePosition();
  },

  /** Funcion que ejecuta la visualizacion del rango cuando se presiona el bonton Rango */
  showRange: function showRangeFn() {
    if (World.placeList.length > 0) {
      World.closePanel();

      /* Update labels on every range movement. */
      World.updateRangeValues();
      World.handlePanelMovements();

      /* Open panel. */
      document.getElementById("panelRange").style.visibility = "visible";
    } else {

      /* No places are visible, because the are not loaded yet. */
      World.updateStatusMessage('No hay lugares disponible cerca', true);
    }
  },
};

/** Funcion que recibe los datos del usuario desde el entorno nativo y se los pasa a la funcion miembro de la escena World. */
AR.context.onLocationChanged = function (
  latitude,
  longitude,
  altitude,
  accuracy
) {
  World.locationChanged(latitude, longitude, altitude, accuracy);
};

/** Funcion que detecta cuando se hace click en la pantalla. */
AR.context.onScreenClick = function () {
  World.onScreenClick();
};