(function() {
  'use strict';
  angular.module('app').controller('VisitTodayMapCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state) {
      var addMarkersToMap, clearMarkers, showInfoWindow;
      $scope.title = 'visit.today.title';
      $scope.isUseDistributorFilter = function() {
        return CtrlUtilsService.getWho() !== 'distributor';
      };
      $scope.mustSelectDistributor = function() {
        return $scope.isUseDistributorFilter() && ($scope.filter.distributorId == null);
      };
      $scope.afterFilterLoad = function() {};
      clearMarkers = function() {
        var latLng;
        if ($scope.markers != null) {
          staticClearMarkers($scope.map, $scope.markers);
          $scope.markers = null;
          if ($scope.map != null) {
            latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude);
            $scope.map.setCenter(latLng);
            return $scope.map.setZoom(CtrlUtilsService.getDefaultZoom());
          }
        }
      };
      showInfoWindow = function(latLng) {
        var content, detailLink, i, marker, _i, _len, _ref;
        if ($scope.markers != null) {
          _ref = $scope.markers;
          for (i = _i = 0, _len = _ref.length; _i < _len; i = ++_i) {
            marker = _ref[i];
            if (marker.getPosition().lat() === latLng.lat() && marker.getPosition().lng() === latLng.lng()) {
              content = '<strong> ' + $scope.records[i].name + ' </strong><br/>';
              if ($scope.records[i].visitInfo != null) {
                detailLink = '#/visit-detail?' + 'id=' + $scope.records[i].visitInfo.id + '&' + 'parent=visit-today-map&' + 'filter=' + $scope.getFilterAsString();
                content = content + '<a class="underline-link" href="' + detailLink + '">' + $filter('translate')('visit.today.view.detail') + '</a>';
              } else {
                content = content + $filter('translate')('visit.today.not.visited');
              }
              $scope.infowindow.setContent(content);
              $scope.infowindow.open($scope.map, marker.getPosition());
              return;
            }
          }
        }
        return $scope.infowindow.close();
      };
      addMarkersToMap = function() {
        var bounds, i, latLng, marker, record, _i, _len, _ref;
        if ($scope.map != null) {
          if (($scope.records != null) && $scope.records.length > 0) {
            $scope.markers = [];
            $scope.infowindow = new MyInfoWindow();
            bounds = new MyLatLngBounds();
            _ref = $scope.records;
            for (i = _i = 0, _len = _ref.length; _i < _len; i = ++_i) {
              record = _ref[i];
              latLng = new MyLatLng(record.location.latitude, record.location.longitude);
              bounds.extend(latLng);
              marker = new MyMarker($filter('translate')('location'));
              staticAddMarkerToMap($scope.map, marker);
              marker.setPosition(latLng);
              marker.setDraggable(false);
              if (record.visitInfo != null) {
                marker.setIcon('images/marker-ok.png');
              } else {
                marker.setIcon('images/marker-warning.png');
              }
              marker.addClickListener(function(latLng) {
                return showInfoWindow(latLng);
              });
              $scope.markers.push(marker);
            }
            return $scope.map.fitBounds(bounds);
          } else {
            latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude);
            $scope.map.setCenter(latLng);
            return $scope.map.setZoom(CtrlUtilsService.getDefaultZoom());
          }
        }
      };
      staticOnMapReady($scope, function(map) {
        $scope.map = map;
        return addMarkersToMap();
      });
      $scope.distributorFilterChange = function() {
        $scope.filter.salesmanId = null;
        return $scope.filterChange();
      };
      $scope.filterChange = function() {
        return $scope.reloadForNewFilter();
      };
      $scope.reloadData = function() {
        var params;
        if (!$scope.mustSelectDistributor()) {
          LoadingUtilsService.loadSalesmenByDistributor($scope.who, $scope.filter.distributorId, $scope.loadStatus.getStatusByDataName('salesmen'), function(list) {
            return $scope.salesmen = list;
          });
          if ($scope.filter.salesmanId != null) {
            clearMarkers();
            params = {
              who: $scope.who,
              category: 'visit',
              subCategory: 'today',
              param: 'by-salesman',
              salesmanId: $scope.filter.salesmanId
            };
            return CtrlUtilsService.loadListData(params, $scope.loadStatus.getStatusByDataName('data'), function(records, count) {
              $scope.records = records;
              $scope.count = count;
              return addMarkersToMap();
            }, function(error) {
              if (error.status === 400) {
                return $state.go('404');
              } else {
                return toast.logError($filter('translate')('loading.error'));
              }
            });
          }
        }
      };
      CtrlInitiatorService.initFilterViewCtrl($scope);
      $scope.addInitFunction(function() {
        $scope.map = null;
        $scope.markers = null;
        if ($scope.isUseDistributorFilter()) {
          LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            return $scope.distributors = list;
          });
        }
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=visit-today-map-ctrl.js.map
