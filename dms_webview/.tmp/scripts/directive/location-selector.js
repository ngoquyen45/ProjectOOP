(function() {
  'use strict';
  angular.module('app.directives').controller('locationSelectorCtrl', [
    '$scope', '$rootScope', 'CtrlUtilsService', '$filter', function($scope, $rootScope, CtrlUtilsService, $filter) {
      var clearMarker, createMarker, ngModelCtrl, refresh;
      ngModelCtrl = {
        $setViewValue: angular.noop
      };
      $scope.location = null;
      $scope.map = null;
      $scope.marker = null;
      this.init = function(ngModelCtrl_) {
        ngModelCtrl = ngModelCtrl_;
        return $rootScope.$broadcast('locationSelectorReady', $scope.elementId);
      };
      staticOnMapReady($scope, function(map) {
        var latLng;
        $scope.map = map;
        if (CtrlUtilsService.getUserInfo().location != null) {
          latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude);
          $scope.map.setCenter(latLng);
          $scope.map.setZoom(CtrlUtilsService.getDefaultZoom());
        }
        if ($scope.location != null) {
          latLng = new MyLatLng($scope.location.latitude, $scope.location.longitude);
          createMarker(latLng);
        }
        return $scope.map.addDoubleClickListener(function(myLatLng) {
          ngModelCtrl.$setViewValue({
            latitude: myLatLng.lat(),
            longitude: myLatLng.lng()
          });
          ngModelCtrl.$render();
          return refresh();
        });
      });
      $scope.$on('refreshLocationSelector', function(event, dto) {
        var latLng;
        if ((dto.id == null) || dto.id === $scope.elementId) {
          $scope.location = dto.location;
          if ($scope.map != null) {
            if ($scope.location != null) {
              latLng = new MyLatLng($scope.location.latitude, $scope.location.longitude);
              return createMarker(latLng);
            } else {
              return clearMarker();
            }
          }
        }
      });
      refresh = function() {
        var latLng;
        $scope.location = ngModelCtrl.$viewValue;
        $scope.$apply();
        if ($scope.map != null) {
          if ($scope.location != null) {
            latLng = new MyLatLng($scope.location.latitude, $scope.location.longitude);
            return createMarker(latLng);
          } else {
            return clearMarker();
          }
        }
      };
      createMarker = function(latLng) {
        var markerTitle, _ref;
        if ($scope.map != null) {
          clearMarker();
          markerTitle = (_ref = $scope.markerTitle) != null ? _ref : 'location';
          $scope.marker = new MyMarker($filter('translate')(markerTitle));
          $scope.marker.setPosition(latLng);
          $scope.marker.setDraggable(true);
          staticAddMarkerToMap($scope.map, $scope.marker);
          $scope.map.panTo($scope.marker.getPosition());
          $scope.map.setZoom(CtrlUtilsService.getDefaultZoom() + 1);
          return $scope.marker.addDragEndListener(function() {
            ngModelCtrl.$setViewValue({
              latitude: $scope.marker.getPosition().lat(),
              longitude: $scope.marker.getPosition().lng()
            });
            ngModelCtrl.$render();
            return refresh();
          });
        }
      };
      return clearMarker = function() {
        var latLng;
        if ($scope.marker != null) {
          staticClearMarkers($scope.map, $scope.marker);
          $scope.marker = null;
          if ($scope.map != null) {
            if (CtrlUtilsService.getUserInfo().location != null) {
              latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude);
              $scope.map.setCenter(latLng);
              return $scope.map.setZoom(CtrlUtilsService.getDefaultZoom());
            }
          }
        }
      };
    }
  ]).directive('locationSelector', [
    function() {
      return {
        restrict: 'E',
        scope: {
          markerTitle: '='
        },
        replace: true,
        require: ['locationSelector', '?ngModel'],
        templateUrl: 'views/directive/location-selector.html',
        controller: 'locationSelectorCtrl',
        link: function(scope, element, attrs, controllers) {
          var myCtrl, ngModelCtrl;
          myCtrl = controllers[0];
          ngModelCtrl = controllers[1];
          scope.elementId = $(element).attr('id');
          if (ngModelCtrl != null) {
            return myCtrl.init(ngModelCtrl);
          }
        }
      };
    }
  ]);

}).call(this);

//# sourceMappingURL=location-selector.js.map
