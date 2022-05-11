class Marker {
  constructor(place) {
    this.place = place;
    this.isSelected = false;

    /** Crea el lugar con los datos geograficos */
    let markerLocation = new AR.GeoLocation(
      this.place.latitud,
      this.place.longitud,
      this.place.altitud
    );

    /** Crea la imagen de fondo del lugar */
    this.placeImageDrawableIdle = new AR.ImageDrawable(
      new AR.ImageResource(this.place.fondo),
      2.5,
      {
        zOrder: 0,
        opacity: 1.0,
        onClick: Marker.prototype.getOnClickTrigger(this),
      }
    );

    /** Crea la imagen de fondo del lugar seleccionado */
    this.placeImageDrawableSelected = new AR.ImageDrawable(
      new AR.ImageResource(this.place.fondoSeleccionado),
      2.5,
      {
        zOrder: 0,
        opacity: 0.0,
        onClick: null,
      }
    );

    /** Crea la imagen del lugar real */
    this.placeImageThumbnail = new AR.ImageDrawable(new AR.ImageResource(this.place.miniatura), 2.5, {
      zOrder: 0,
      enabled: false,
      translate: {
        y: 3.3,
      },
    });

    /** Crea la etiqueta del nombre del lugar */
    this.titleLabel = new AR.Label(this.place.nombre.trunc(20), 0.6, {
      zOrder: 1,
      translate: {
        y: 1.6,
      },
      style: {
        textColor: "#FFFFFF",
        fontStyle: AR.CONST.FONT_STYLE.BOLD,
        backgroundColor: "#00813A",
      },
    });

    /** Crea la etiqueta de la distancia del lugar */
    this.distanceLabel = new AR.Label("", 0.5, {
      zOrder: 1,
      translate: {
        y: -1.8,
      },
      style: {
        textColor: "#FFFFFF",
        fontStyle: AR.CONST.FONT_STYLE.BOLD,
        backgroundColor: "#00813A",
      },
    });

    /** Crea la imagen indicador de direccion. */
    this.directionIndicatorDrawable = new AR.ImageDrawable(
      World.indicatorMarkerDrawable,
      0.18,
      {
        enabled: false,
        verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP,
      }
    );

    /** Crea la imagen de los lugares en el radar */
    this.radarCircle = new AR.Circle(0.03, {
      horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
      opacity: 0.8,
      style: {
        fillColor: "#ffffff",
      },
    });

    /** Crea la imagen de los lugares en el radar seleccionados */
    this.radarCircleSelected = new AR.Circle(0.03, {
      horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
      opacity: 0.8,
      style: {
        fillColor: "#0066ff",
      },
    });

    this.radardrawables = [];
    this.radardrawables.push(this.radarCircle);

    this.radardrawablesSelected = [];
    this.radardrawablesSelected.push(this.radarCircleSelected);

    /* Crea el objeto geolocalizado pasando los datos necesario creados anteriormente. */
    this.markerObject = new AR.GeoObject(markerLocation, {
      drawables: {
        cam: [
          this.placeImageDrawableIdle,
          this.placeImageDrawableSelected,
          this.placeImageThumbnail,
          this.titleLabel,
          this.distanceLabel,
        ],
        indicator: this.directionIndicatorDrawable,
        radar: this.radardrawables,
      },
    });

    return this;
  }
}

/** Funcion que se herreda del click en el marcador. */
Marker.prototype.getOnClickTrigger = (marker) =>
  function () {
    if (marker.isSelected) {
      Marker.prototype.setDeselected(marker);
    } else {
      Marker.prototype.setSelected(marker);
      try {
        // Envio el marcador seleccionado al World
        World.onMarkerSelected(marker);
      } catch (err) {
        alert(err);
      }
    }

    return true;
  };

/** Funcion para seleccionar el marcador. */
Marker.prototype.setSelected = (marker) => {
  marker.isSelected = true;

  marker.directionIndicatorDrawable.enabled = true;
  marker.placeImageDrawableIdle.opacity = 0.0;
  marker.placeImageDrawableSelected.opacity = 1.0;

  marker.placeImageDrawableIdle.onClick = null;
  marker.placeImageDrawableSelected.onClick =
    Marker.prototype.getOnClickTrigger(marker);
  marker.placeImageThumbnail.enabled = true;
  marker.markerObject.drawables.radar = marker.radardrawablesSelected;

  marker.titleLabel.style.backgroundColor = "#6854E4";
  marker.distanceLabel.style.backgroundColor = "#6854E4";
};

/** Funcion para deselecionar el marcador. */
Marker.prototype.setDeselected = (marker) => {
  marker.isSelected = false;

  marker.directionIndicatorDrawable.enabled = false;
  marker.placeImageDrawableIdle.opacity = 1.0;
  marker.placeImageDrawableSelected.opacity = 0.0;

  marker.placeImageDrawableIdle.onClick =
    Marker.prototype.getOnClickTrigger(marker);
  marker.placeImageDrawableSelected.onClick = null;
  marker.placeImageThumbnail.enabled = false;
  marker.markerObject.drawables.radar = marker.radardrawables;

  marker.titleLabel.style.backgroundColor = "#00813A";
  marker.distanceLabel.style.backgroundColor = "#00813A";
};

/* Will truncate all strings longer than given max-length "n". e.g. "foobar".trunc(3) -> "foo...". */
String.prototype.trunc = function (n) {
  return this.substr(0, n - 1) + (this.length > n ? "..." : "");
};
