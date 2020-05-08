(function() {
  this.staticOnMapReady = function($scope, callback) {
    return $scope.$on('mapInitialized', function(event, map) {
      if (jQuery.isFunction(callback)) {
        return callback(new MyMap(map));
      }
    });
  };

  this.staticAddMarkerToMap = function(myMap, myMarker) {
    if ((myMap != null) && (myMarker != null)) {
      return myMarker.getObject().setMap(myMap.getObject());
    }
  };

  this.staticClearMarkers = function(myMap, myMarkers) {
    var myMarker, _i, _len, _results;
    if (myMarkers != null) {
      if (Array.isArray(myMarkers)) {
        _results = [];
        for (_i = 0, _len = myMarkers.length; _i < _len; _i++) {
          myMarker = myMarkers[_i];
          _results.push(myMarker.getObject().setMap(null));
        }
        return _results;
      } else {
        return myMarkers.getObject().setMap(null);
      }
    }
  };

  this.MyMap = (function() {
    function MyMap(map) {
      this.map = map;
    }

    MyMap.prototype.setCenter = function(myLatLng) {
      return this.map.setCenter(myLatLng.getObject());
    };

    MyMap.prototype.panTo = function(myLatLng) {
      return this.map.panTo(myLatLng.getObject());
    };

    MyMap.prototype.setZoom = function(zoom) {
      return this.map.setZoom(zoom);
    };

    MyMap.prototype.fitBounds = function(myLatLngBounds) {
      return this.map.fitBounds(myLatLngBounds.getObject());
    };

    MyMap.prototype.addDoubleClickListener = function(callback) {
      return this.map.addListener('dblclick', function(event) {
        var lat, lng;
        lat = event.latLng.lat();
        lng = event.latLng.lng();
        if (jQuery.isFunction(callback)) {
          return callback(new MyLatLng(lat, lng));
        }
      });
    };

    MyMap.prototype.getObject = function() {
      return this.map;
    };

    return MyMap;

  })();

  this.MyMarker = (function() {
    function MyMarker(title) {
      this.marker = new google.maps.Marker({
        title: title
      });
      this.marker.setAnimation(google.maps.Animation.DROP);
    }

    MyMarker.prototype.getPosition = function() {
      return new MyLatLng(this.marker.getPosition().lat(), this.marker.getPosition().lng());
    };

    MyMarker.prototype.setPosition = function(myLatLng) {
      return this.marker.setPosition(myLatLng.getObject());
    };

    MyMarker.prototype.setIcon = function(icon) {
      return this.marker.setIcon(icon);
    };

    MyMarker.prototype.setDraggable = function(draggable) {
      return this.marker.setDraggable(draggable);
    };

    MyMarker.prototype.addClickListener = function(callback) {
      return this.marker.addListener('click', function(event) {
        var lat, lng;
        lat = event.latLng.lat();
        lng = event.latLng.lng();
        if (jQuery.isFunction(callback)) {
          return callback(new MyLatLng(lat, lng));
        }
      });
    };

    MyMarker.prototype.addDragEndListener = function(callback) {
      return this.marker.addListener('dragend', function() {
        if (jQuery.isFunction(callback)) {
          return callback();
        }
      });
    };

    MyMarker.prototype.getObject = function() {
      return this.marker;
    };

    return MyMarker;

  })();

  this.MyLatLng = (function() {
    function MyLatLng(latitude, longitude) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.latLng = new google.maps.LatLng(this.latitude, this.longitude);
    }

    MyLatLng.prototype.lat = function() {
      return this.latitude;
    };

    MyLatLng.prototype.lng = function() {
      return this.longitude;
    };

    MyLatLng.prototype.getObject = function() {
      return this.latLng;
    };

    return MyLatLng;

  })();

  this.MyLatLngBounds = (function() {
    function MyLatLngBounds() {
      this.LatLngBounds = new google.maps.LatLngBounds();
    }

    MyLatLngBounds.prototype.extend = function(myLatLng) {
      return this.LatLngBounds.extend(myLatLng.getObject());
    };

    MyLatLngBounds.prototype.getObject = function() {
      return this.LatLngBounds;
    };

    return MyLatLngBounds;

  })();

  this.MyInfoWindow = (function() {
    function MyInfoWindow() {
      this.infoWindow = new google.maps.InfoWindow();
    }

    MyInfoWindow.prototype.setContent = function(content) {
      return this.infoWindow.setContent(content);
    };

    MyInfoWindow.prototype.open = function(myMap, myLatLng) {
      this.infoWindow.setPosition(myLatLng.getObject());
      return this.infoWindow.open(myMap.getObject());
    };

    MyInfoWindow.prototype.close = function() {
      return this.infoWindow.close();
    };

    return MyInfoWindow;

  })();

  angular.module("app").run([
    "$templateCache", function($templateCache) {
      return $templateCache.put('map.html', '<map class="ui-map"></map>');
    }
  ]);

}).call(this);

//# sourceMappingURL=google-map.js.map
