class Marker {
  constructor(place) {
    this.placeAr = place;

    this.markerLocation = new AR.GeoLocation(
      this.placeAr.latitude,
      this.placeAr.longitude,
      this.placeAr.altitude
    );

    this.imageDrawable = new AR.ImageDrawable(World.locationDrawable, 4, {
      zOrder: 0,
      opacity: 1.0,
      // onClick: Marker.prototype.getOnClickTrigger(this),
    });

    let iamgeLeft = new AR.ImageResource(this.placeAr.slider[0]);
    let imageRight = new AR.ImageResource(this.placeAr.slider[1]);
    let imageThumbnail = new AR.ImageResource(this.placeAr.thumbnail);

    this.imageThumbnailDrawable = new AR.ImageDrawable(imageThumbnail, 3, {
      zOrder: -1,
      translate: {
        y: 4,
      },
      enabled: true,
    });

    this.imageLeftDrawable = new AR.ImageDrawable(iamgeLeft, 3, {
      zOrder: -1,
      translate: {
        y: 4,
        x: 6,
      },
      enabled: true,
    });

    this.imageRightDrawable = new AR.ImageDrawable(imageRight, 3, {
      zOrder: -1,
      translate: {
        y: 4,
        x: -6,
      },
      enabled: true,
    });

    let labelName = new AR.Label(this.placeAr.name, 0.8, {
      zOrder: 1,
      translate: {
        y: 2.2,
      },
      style: {
        textColor: "#FFFFFF",
        fontStyle: AR.CONST.FONT_STYLE.BOLD,
      },
    });

    this.labelDescription = new AR.Label(this.placeAr.description.trunc(51), 0.8, {
      zOrder: 1,
      translate: {
        y: -3.5,
      },
      style: {
        textColor: "#FFFFFF",
        fontStyle: AR.CONST.FONT_STYLE.BOLD,
      },
    });

    this.indicatorDrawableDirection = new AR.ImageDrawable(
      World.markerDrawableDirectionIndicator,
      0.2,
      {
        enabled: true,
        verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP,
      }
    );

    this.markerObject = new AR.GeoObject(this.markerLocation, {
      drawables: {
        cam: [
          this.imageDrawable,
          this.imageThumbnailDrawable,
          this.imageLeftDrawable,
          this.imageRightDrawable,
          this.labelDescription,
          labelName,
        ],
        indicator: this.indicatorDrawableDirection,
      },
    });

    return this;
  }
}

Marker.prototype.getOnClickTrigger = (marker) => {
  return () => {
    if (marker.isSelected) {
      Marker.prototype.setDeselected(marker);
    } else {
      Marker.prototype.setSelected(marker);
      try {
        World.onMarkerSelected(marker);
      } catch (err) {
        alert(err);
      }
    }
    return true;
  };
};

Marker.prototype.setSelected = (marker) => {
  marker.isSelected = true;

  marker.imageDrawable.opacity = 0.0;
  marker.imageDrawableSelected.opacity = 1.0;
  marker.imageDrawable.onClick = null;
  marker.imageDrawableSelected.onClick =
    Marker.prototype.getOnClickTrigger(marker);
  marker.indicatorDrawableDirection.enabled = true;
  marker.imageThumbnailDrawable.enabled = true;
};

Marker.prototype.setDeselected = (marker) => {
  marker.isSelected = false;

  marker.imageDrawable.opacity = 1.0;
  marker.imageDrawableSelected.opacity = 0.0;
  marker.imageDrawable.onClick = Marker.prototype.getOnClickTrigger(marker);
  marker.imageDrawableSelected.onClick = null;
  marker.indicatorDrawableDirection.enabled = false;
  marker.imageThumbnailDrawable.enabled = false;
};

String.prototype.trunc = function (n) {
  return this.substr(0, n - 1) + (this.length > n ? "..." : "");
};
