/* Implementacion de la experiencia en Realidad Aumentada ("World"). */
let World = {
  /* Sera true cuando los datos esten listos. */
  initiallyLoadedData: false,

  /* Assets para los lugares. */
  indicatorMarkerDrawable: null,
  buttonMoreDrawable: null,

  /* Lista de AR.GeoObjects que se estan mostrando en la escena (World). */
  placeList: [],

  /* El ultimo marcado que esta cercano al usuario. */
  currentMarker: null,

  /* Funcion que recibe las actualizaciones de ubicacion desde el entorno nativo binding.architectView.setLocation(). */
  locationChanged: (latitude, longitude, altitude, accuracy) => {
  
    /** Comprueba si ya estan cargados los datos. */
    if (!World.initiallyLoadedData) {
      World.initiallyLoadedData = true;
    } else {
      /** Llama a la funcion que actualiza la distancia de los marcadores periodicamente. */
      World.checkDistanceIfUserIsNear();
    }
  },

  /** Lista de lugares que se reciben de la base de datos desde nativo. **/
  setPlacesFromDataBase: (places) => {
    let listOfPlaces = [];

    /** Se asigna los marcadores para cada categoria de lugar */
    let imageCategory = "";
    let imageCategorySelected = "";

    /** Se recorre la lista de lugares recibidos en formato Json. */
    places.forEach((place) => {
      switch (place.categoria) {
        case "parque":
          imageCategory = "assets/location_park.png";
          imageCategorySelected = "assets/location_park_selected.png";
          break;
        case "histórico":
          imageCategory = "assets/location_history.png";
          imageCategorySelected = "assets/location_history_selected.png";
          break;
        case "naturaleza":
          imageCategory = "assets/location_nature.png";
          imageCategorySelected = "assets/location_nature_selected.png";
          break;
        case "deportivo":
          imageCategory = "assets/location_sports.png";
          imageCategorySelected = "assets/location_sports_selected.png";
          break;
        case "hospedaje":
          imageCategory = "assets/location_hotel.png";
          imageCategorySelected = "assets/location_hotel_selected.png";
          break;
        case "entretenimiento":
          imageCategory = "assets/location_entertaiment.png";
          imageCategorySelected = "assets/location_entertaiment_selected.png";
          break;
      }

      /** Se mapea la informacion que se necesita en un objeto JavaScript. */
      let marker = {
        id: place.id,
        nombre: place.nombre,
        latitud: place.latitud,
        longitud: place.longitud,
        altitud: place.altitud,
        categoria: place.categoria,
        descripcion: place.descripcion,
        direccion: place.direccion,
        fondo: imageCategory,
        fondoSeleccionado: imageCategorySelected,
        miniatura: place.miniatura,
      };

      /** Se almacena los objetos en la lista. */
      listOfPlaces.push(marker);
    });

    /** Se pasa esa lista para que se cargue en la escena World. */
    World.loadPlacesInWorldScene(listOfPlaces);
  },

  /* Funcion que se llama para agregar los geopoints en AR. */
  loadPlacesInWorldScene: (placesAR) => {
    /** Destruye todos los GeoLocation para resetear la escena */
    AR.context.destroyAll();

    PlaceRadar.show();

    /* Lista vacia de lugares en la variable miembro de World. */
    World.placeList = [];

    /** Recurso de imagen usado para indicar la direccion del lugar. */
    World.indicatorMarkerDrawable = new AR.ImageResource(
      "assets/indicator_ar.png"
    );

    /** Recurso de imagen usado para el boton de mas informacion */
    World.buttonMoreDrawable = new AR.ImageResource("assets/more_info.png");

    /* Se recorre la lista de lugares, se los almacena en la variable miembro World. */
    placesAR.forEach((placeAR) => {
      World.placeList.push(new Marker(placeAR));
    });

    /* Mensaje a modo de feedback para conocer los lugares cargados en la escena World. */
    World.updateStatusMessage(World.placeList.length + " lugares cargados");
  },

  /** Funcion que recibe el marcador que esta cerca del usuario */
  onMarkerSelected: (marker) => {
    World.currentMarker = marker;
  },

  /* Funcion que desmarca al marcador cuando el usuario esta lejos de rango. */
  onMarkerDeselected: (marker) => {
    if (World.currentMarker != null) {
      if (World.currentMarker.place.id === marker.place.id) {
        World.currentMarker = null;
      }
    }
  },

  /* Funcion que detecta el click del usuario en el boton. */
  onButtonMoreInfoClick: (marker) => {
    let placeClicked = {
      id: marker.place.id,
    };
    AR.platform.sendJSONObject(placeClicked);
  },

  /** Funcion que permite establecer/actualizar todos los marcadores  */
  updateDistanceToUserValues: () => {
    World.placeList.forEach((marker) => {
      marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();

      let distanceToUserValue =
        marker.distanceToUser > 999
          ? (marker.distanceToUser / 1000).toFixed(2) + " km"
          : Math.round(marker.distanceToUser) + " m";

      marker.distanceLabel.text = distanceToUserValue;
    });
  },

  /** Funcion que se comunica con nativo para enviar el lugar detectado. */
  onMarkerDetected: () => {
    if (World.currentMarker != null) {
      let placeSelected = {
        place: World.currentMarker.place.nombre,
      };
      AR.platform.sendJSONObject(placeSelected);
    } else {
      let placeSelected = {
        place: "",
      };
      AR.platform.sendJSONObject(placeSelected);
    }
  },

  /* Mensaje de estado que se muestra en el boton "i"- alienado en la parte inferior centro. */
  updateStatusMessage: (message, isWarning) => {
    document.getElementById("popupButtonImage").src = isWarning
      ? "assets/warning_icon.png"
      : "assets/info_icon.png";
    document.getElementById("popupButtonTooltip").innerHTML = message;
  },

  /** Funcion para mostrar un error */
  onError: (error) => {
    alert(error);
  },

  /** Funcion del eslider en nativo para limitar el rango de visualizacion de los marcadores */
  sliderValueRange: (value) => {
    let distanceVisible = 0;
    switch (value) {
      case 0:
        distanceVisible = 500;
        break;
      case 1:
        distanceVisible = 1000;
        break;
      case 2:
        distanceVisible = 2000;
        break;
      case 3:
        distanceVisible = 5000;
        break;
      case 4:
        distanceVisible = 10000;
        break;
      case 5:
        distanceVisible = 20000;
        break;
    }
    AR.context.scene.cullingDistance = distanceVisible;
    PlaceRadar.setMaxDistance(distanceVisible);

    /* Update labels on every range movement. */
    PlaceRadar.updatePosition();
  },

  /** Funcion que comprueba si el usuario esta cerca de un marcador */
  checkDistanceIfUserIsNear: () => {
    World.updateDistanceToUserValues();

    World.placeList.forEach((marker) => {
      if (marker.distanceToUser <= 13) {
        marker.setSelected(marker);
      } else {
        marker.setDeselected(marker);
      }
      World.onMarkerDetected();
    });
  },
};

/** Funcion que recibe los datos del usuario desde el entorno nativo y se los pasa a la funcion miembro de la escena World. */
AR.context.onLocationChanged = (latitude, longitude, altitude, accuracy) => {
  World.locationChanged(latitude, longitude, altitude, accuracy);
};
