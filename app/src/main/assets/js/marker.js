class Marker {
  constructor(place) {
    this.place = place;

    let markerLocation = new AR.GeoLocation(
      place.latitud,
      place.longitud,
      place.altitud
    );

    let markerImageDrawableIdle = new AR.ImageDrawable(
      World.markerDrawableIdle,
      2.5,
      {
        zOrder: 0,
        opacity: 1.0,
      }
    );

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

    let descriptionLabel = new AR.Label(place.categoria.trunc(15), 0.5, {
      zOrder: 1,
      translate: {
        y: -0.55,
      },
      style: {
        textColor: "#FFFFFF",
      },
    });

    /* Create GeoObject. */
    this.markerObject = new AR.GeoObject(markerLocation, {
      drawables: {
        cam: [markerImageDrawableIdle, titleLabel, descriptionLabel],
      },
    });

    return this;
  }
}

/* Will truncate all strings longer than given max-length "n". e.g. "foobar".trunc(3) -> "foo...". */
String.prototype.trunc = function (n) {
  return this.substr(0, n - 1) + (this.length > n ? "..." : "");
};
