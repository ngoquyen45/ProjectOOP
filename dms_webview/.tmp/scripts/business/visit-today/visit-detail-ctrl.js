(function() {
  'use strict';
  angular.module('app').controller('VisitDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, ADDRESS_BACKEND) {
      var addMarkerToMap;
      $scope.title = 'visit.detail.title';
      $scope.defaultBackState = 'visit-today';
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      $scope.isClosed = function() {
        return ($scope.record != null) && $scope.record.closed;
      };
      $scope.getTimeDisplay = function() {
        var duration, minute, second, time;
        if (($scope.record != null) && ($scope.record.duration != null) && ($scope.record.startTime != null) && ($scope.record.endTime != null)) {
          minute = parseInt($scope.record.duration / 60000);
          second = parseInt(($scope.record.duration / 1000) % 60);
          duration = minute + 'm ' + second + 's';
          time = $filter('isoTime')($scope.record.startTime) + ' - ' + $filter('isoTime')($scope.record.endTime);
          return time + ' (' + duration + ')';
        } else {
          return '';
        }
      };
      $scope.getDurationDisplay = function() {
        var minute, second;
        if (($scope.record != null) && ($scope.record.duration != null)) {
          minute = parseInt($scope.record.duration / 60000);
          second = parseInt(($scope.record.duration / 1000) % 60);
          return minute + 'm ' + second + 's';
        } else {
          return '';
        }
      };
      $scope.getStartEndDisplay = function() {
        if (($scope.record != null) && ($scope.record.startTime != null) && ($scope.record.endTime != null)) {
          return $filter('isoTime')($scope.record.startTime) + ' - ' + $filter('isoTime')($scope.record.endTime);
        } else {
          return '';
        }
      };
      $scope.isErrorDistance = function() {
        return !(($scope.record != null) && ($scope.record.locationStatus != null) && $scope.record.locationStatus === 0);
      };
      $scope.getDistanceDisplay = function() {
        if (($scope.record != null) && ($scope.record.locationStatus != null) && ($scope.record.locationStatus === 0 || $scope.record.locationStatus === 1)) {
          if ($scope.record.distance != null) {
            return $filter('number')($scope.record.distance * 1000, 0) + ' m';
          } else {
            return '';
          }
        } else if (($scope.record != null) && ($scope.record.locationStatus != null) && $scope.record.locationStatus === 3) {
          return $filter('translate')('visit.customer.location.undefined');
        } else {
          return $filter('translate')('visit.salesman.location.undefined');
        }
      };
      $scope.hasOrder = function() {
        return ($scope.record != null) && $scope.record.hasOrder;
      };
      $scope.getOrderDisplay = function() {
        if ($scope.hasOrder()) {
          return $filter('number')($scope.record.grandTotal, 0);
        } else {
          return $filter('translate')('visit.no.order');
        }
      };
      $scope.printOrder = function() {
        return CtrlUtilsService.printOrder('order');
      };
      $scope.hasFeedback = function() {
        return ($scope.record != null) && ($scope.record.feedbacks != null) && $scope.record.feedbacks.length > 0;
      };
      $scope.hasPhoto = function() {
        return ($scope.record != null) && ($scope.record.photo != null);
      };
      $scope.getPhoto = function() {
        if (($scope.record != null) && ($scope.record.photo != null)) {
          return ADDRESS_BACKEND + 'image/' + $scope.record.photo;
        }
      };
      staticOnMapReady($scope, function(map) {
        $scope.map = map;
        return addMarkerToMap();
      });
      addMarkerToMap = function() {
        var bounds, latLng;
        if ($scope.map != null) {
          if (($scope.salesmanMarker != null) || ($scope.customerMarker != null)) {
            if ($scope.salesmanMarker != null) {
              staticAddMarkerToMap($scope.map, $scope.salesmanMarker);
            }
            if ($scope.customerMarker != null) {
              staticAddMarkerToMap($scope.map, $scope.customerMarker);
            }
            if (($scope.salesmanMarker != null) && ($scope.customerMarker != null)) {
              bounds = new MyLatLngBounds();
              bounds.extend($scope.salesmanMarker.getPosition());
              bounds.extend($scope.customerMarker.getPosition());
              return $scope.map.fitBounds(bounds);
            } else {
              $scope.map.panTo($scope.salesmanMarker != null ? $scope.salesmanMarker.getPosition() : $scope.customerMarker.getPosition());
              return $scope.map.setZoom(CtrlUtilsService.getDefaultZoom() + 2);
            }
          } else {
            latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude);
            $scope.map.setCenter(latLng);
            return $scope.map.setZoom(CtrlUtilsService.getDefaultZoom());
          }
        }
      };
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'visit',
          id: $stateParams.id
        };
        return CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(record) {
          var latLng;
          $scope.record = record;
          if ($scope.record.location != null) {
            latLng = new MyLatLng($scope.record.location.latitude, $scope.record.location.longitude);
            $scope.salesmanMarker = new MyMarker($filter('translate')('salesman.location'));
            $scope.salesmanMarker.setPosition(latLng);
            $scope.salesmanMarker.setDraggable(false);
            $scope.salesmanMarker.setIcon('images/marker-salesman.png');
          }
          if ($scope.record.customerLocation != null) {
            latLng = new MyLatLng($scope.record.customerLocation.latitude, $scope.record.customerLocation.longitude);
            $scope.customerMarker = new MyMarker($filter('translate')('customer.location'));
            $scope.customerMarker.setPosition(latLng);
            $scope.customerMarker.setDraggable(false);
            $scope.customerMarker.setIcon('images/marker-customer.png');
          }
          return addMarkerToMap();
        }, function(error) {
          if (error.status === 400) {
            return $state.go('404');
          } else {
            return toast.logError($filter('translate')('loading.error'));
          }
        });
      };
      $scope.addInitFunction(function() {
        if ($stateParams.id == null) {
          $state.go('404');
        }
        $scope.map = null;
        $scope.salesmanMarker = null;
        $scope.customerMarker = null;
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=visit-detail-ctrl.js.map
