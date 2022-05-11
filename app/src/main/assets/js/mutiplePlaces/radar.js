/** Objeto radar. */
let PlaceRadar = {
  /** Funcion para quitar el radar. */
  hide: () => {
    AR.radar.enabled = false;
  },

  /** Funcion que muestra el radar en la escena World. */
  show: () => {
    /* Se obtiene el contenedor del index.html. */
    AR.radar.container = document.getElementById("radarContainer");

    /* Se crea el recurso de imagen del radar. */
    AR.radar.background = new AR.ImageResource("assets/radar_bg.png", {
      onError: World.onError,
    });

    /** Se crear el recurso para la imagen de indicador de norte. */
    AR.radar.northIndicator.image = new AR.ImageResource(
      "assets/radar_north.png",
      {
        onError: World.onError,
      }
    );

    /** Se mide el centro del radar en los ejes X, Y  y el radio*/
    AR.radar.centerX = 0.5;
    AR.radar.centerY = 0.5;

    AR.radar.radius = 0.3;
    AR.radar.northIndicator.radius = 0.0;

    /** Se habilita el radar. */
    AR.radar.enabled = true;
  },

  /** Funcion para actualizar la posicion de los puntos en el radar* */
  updatePosition: () => {
    if (AR.radar.enabled) {
      AR.radar.notifyUpdateRadarPosition();
    }
  },

  setMaxDistance: (maxDistanceMeters) => {
    AR.radar.maxDistance = maxDistanceMeters;
  },
};
