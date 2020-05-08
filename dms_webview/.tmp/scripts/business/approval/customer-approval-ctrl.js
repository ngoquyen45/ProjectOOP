(function() {
  'use strict';
  angular.module('app').controller('CustomerPendingCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'customer.approval.title';
      $scope.filterChange = function() {
        $scope.filter.currentPage = 1;
        return $scope.pageChange();
      };
      $scope.goToDetail = function(record) {
        return $state.go('customer-approval', {
          id: record.id,
          filter: $stateParams.filter
        });
      };
      $scope.getReloadDataParams = function(params) {
        params.category = 'customer';
        params.subCategory = 'pending';
        return params;
      };
      CtrlInitiatorService.initPagingFilterViewCtrl($scope);
      $scope.addInitFunction(function() {
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]).controller('CustomerApprovalCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      var addMarkerToMap, doAction;
      $scope.title = 'customer.approval.title';
      $scope.defaultBackState = 'customer-pending';
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      doAction = function(action, callbackSuccess, callbackFailure) {
        var params, status;
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'customer',
          subCategory: 'pending',
          id: $stateParams.id,
          action: action
        };
        return CtrlUtilsService.doPut(params, status, null, callbackSuccess, callbackFailure, action);
      };
      $scope.approve = function() {
        return doAction('approve', function() {
          return $state.go('customer-schedule-single', {
            id: $stateParams.id
          });
        });
      };
      $scope.reject = function() {
        return doAction('reject', function() {
          return $scope.back();
        });
      };
      staticOnMapReady($scope, function(map) {
        $scope.map = map;
        return addMarkerToMap();
      });
      addMarkerToMap = function() {
        var latLng;
        if ($scope.map != null) {
          if ($scope.marker != null) {
            staticAddMarkerToMap($scope.map, $scope.marker);
            $scope.map.panTo($scope.marker.getPosition());
            return $scope.map.setZoom(CtrlUtilsService.getDefaultZoom() + 1);
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
          category: 'customer',
          subCategory: 'pending',
          id: $stateParams.id
        };
        return CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(record) {
          var latLng;
          $scope.record = record;
          if ($scope.record.location != null) {
            latLng = new MyLatLng($scope.record.location.latitude, $scope.record.location.longitude);
            $scope.marker = new MyMarker($filter('translate')('customer.location'));
            $scope.marker.setPosition(latLng);
            $scope.marker.setDraggable(false);
            $scope.marker.setIcon('images/marker-customer.png');
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
        $scope.marker = null;
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=customer-approval-ctrl.js.map
