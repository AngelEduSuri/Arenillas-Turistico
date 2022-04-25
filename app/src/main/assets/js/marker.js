class Marker {
  constructor(place) {
    this.place = place;

    /** Crea el lugar con los datos geograficos */
    let markerLocation = new AR.GeoLocation(
      place.latitud,
      place.longitud,
      place.altitud
    );

    /** Crea la imagen de fondo del lugar */
    this.placeImageDrawableIdle = new AR.ImageDrawable(
      World.markerDrawableIdle,
      2.5,
      {
        zOrder: 0,
        opacity: 1.0,
        onClick: Marker.prototype.getOnClickTrigger(this),
      }
    );

    /** Crea la imagen de fondo del lugar seleccionado */
    this.placeImageDrawableSelected = new AR.ImageDrawable(
      World.markerDrawableSelected,
      2.5,
      {
        zOrder: 0,
        opacity: 0.0,
        onClick: null,
      }
    );

    /** Crea la etiqueta del nombre del lugar */
    let titleLabel = new AR.Label(place.nombre.trunc(20), 0.6, {
      zOrder: 1,
      translate: {
        y: 0.55,
      },
      style: {
        textColor: "#FFFFFF",
        fontStyle: AR.CONST.FONT_STYLE.BOLD,
      },
    });

    /** Crea la etiqueta de la descripcion del lugar */
    let descriptionLabel = new AR.Label(place.categoria.trunc(15), 0.5, {
      zOrder: 1,
      translate: {
        y: -0.55,
      },
      style: {
        textColor: "#FFFFFF",
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
          titleLabel,
          descriptionLabel,
        ],
        indicator: this.directionIndicatorDrawable,
        radar: this.radardrawables,
      },
    });

    return this;
  }
}

/** Funcion que se herreda del click en el marcador. */
Marker.prototype.getOnClickTrigger = function (marker) {
  return function () {
    /** Comprueba si el marcador esta seleccionado. */
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
};

/** Funcion para seleccionar el marcador. */
Marker.prototype.setSelected = function (marker) {
  marker.isSelected = true;

  marker.directionIndicatorDrawable.enabled = true;
  marker.placeImageDrawableIdle.opacity = 0.0;
  marker.placeImageDrawableSelected.opacity = 1.0;

  marker.placeImageDrawableIdle.onClick = null;
  marker.placeImageDrawableSelected.onClick =
    Marker.prototype.getOnClickTrigger(marker);
  marker.markerObject.drawables.radar = marker.radardrawablesSelected;
};

/** Funcion para deselecionar el marcador. */
Marker.prototype.setDeselected = function (marker) {
  marker.isSelected = false;

  marker.directionIndicatorDrawable.enabled = false;
  marker.placeImageDrawableIdle.opacity = 1.0;
  marker.placeImageDrawableSelected.opacity = 0.0;

  marker.placeImageDrawableIdle.onClick =
    Marker.prototype.getOnClickTrigger(marker);
  marker.placeImageDrawableSelected.onClick = null;
  marker.markerObject.drawables.radar = marker.radardrawables;
};

/* Will truncate all strings longer than given max-length "n". e.g. "foobar".trunc(3) -> "foo...". */
String.prototype.trunc = function (n) {
  return this.substr(0, n - 1) + (this.length > n ? "..." : "");
};
