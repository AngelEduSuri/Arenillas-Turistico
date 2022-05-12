/* Implementation of AR-Experience (aka "World"). */
let World = {
  initialLoadData: false,

  listPlaces: [],

  locationDrawable: null,
  markerDrawableDirectionIndicator: null,

  locationChanged: (latitude, longitude, altitude, accuracy) => {
    let location = {
      name: "User",
      latitude: latitude,
      longitude: longitude,
      altitude: altitude,
      accuracy: accuracy,
    };
    document.getElementById("location").innerHTML =
      location.name +
      "= Latitud: " +
      location.latitude +
      " - Longitud: " +
      location.longitude +
      " - Altidud: " +
      location.altitude;

    if (!World.initialLoadData) {
      World.initialLoadData = true;
    }
  },

  getPlacesFromNative: (place) => {
    World.markerDrawableDirectionIndicator = new AR.ImageResource(
      "assets/indicator_ar.png",
      {
        onError: World.onError,
      }
    );

    World.locationDrawable = new AR.ImageResource(
      "assets/location_detail.png",
      {
        onError: World.onError,
      }
    );

    let placeAr = {
      id: place.id,
      name: place.nombre,
      background: place.thumbnail,
      category: place.categoria,
      latitude: place.latitud,
      longitude: place.longitud,
      altitude: place.altitud,
      thumbnail: place.miniatura,
      description: place.descripcion,
      slider: place.imagenes,
    };
    World.loadPlaceInAr(placeAr);
  },

  loadPlaceInAr: (placeAr) => {
    World.listPlaces = [];

    World.listPlaces.push(new Marker(placeAr));

    World.updateStatusMessage("Lugar cargado");
  },

  onMarkerSelected: (marker) => {
    if (World.currentMarker) {
      if (World.currentMarker.placeAr.id === marker.placeAr.id) {
        return;
      }
      World.currentMarker.setDeselected(World.currentMarker);
    }

    marker.setSelected(marker);
    World.currentMarker = marker;
  },

  onScreenClick: () => {
    if (World.currentMarker) {
      World.currentMarker.setDeselected(World.currentMarker);
    }
  },

  updateStatusMessage: (message, isWarning) => {
    document.getElementById("popupButtonImage").src = isWarning
      ? "assets/warning_icon.png"
      : "assets/info_icon.png";
    document.getElementById("popupButtonTooltip").innerHTML = message;
  },

  onError: (error) => {
    alert(error);
  },
};

AR.context.onLocationChanged = (latitude, longitude, altitude, accuracy) => {
  World.locationChanged(latitude, longitude, altitude, accuracy);
};

AR.context.onScreenClick = () => {
  World.onScreenClick();
};

AR.context.scene.maxScalingDistance = 15000;
