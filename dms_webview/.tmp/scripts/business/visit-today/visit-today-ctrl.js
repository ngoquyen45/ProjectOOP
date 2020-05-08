(function() {
  'use strict';
  angular.module('app').controller('VisitTodayCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'visit.today.title';
      $scope.isUseDistributorFilter = function() {
        return CtrlUtilsService.getWho() !== 'distributor';
      };
      $scope.mustSelectDistributor = function() {
        return $scope.isUseDistributorFilter() && ($scope.filter.distributorId == null);
      };
      $scope.getDurationDisplay = function(duration) {
        var minute, second;
        if (duration != null) {
          minute = parseInt(duration / 60000);
          second = parseInt((duration / 1000) % 60);
          return minute + 'm ' + second + 's';
        } else {
          return '';
        }
      };
      $scope.getStartEndDisplay = function(record) {
        if ((record != null) && (record.startTime != null) && (record.endTime != null)) {
          return $filter('isoTime')(record.startTime) + ' - ' + $filter('isoTime')(record.endTime);
        } else {
          return '';
        }
      };
      $scope.isDistanceNormal = function(record) {
        return (record.locationStatus != null) && record.locationStatus === 0;
      };
      $scope.isDistanceTooFar = function(record) {
        return (record.locationStatus != null) && record.locationStatus === 1;
      };
      $scope.isLocationSalesmanUndefined = function(record) {
        return (record.locationStatus != null) && record.locationStatus === 2;
      };
      $scope.isLocationCustomerUndefined = function(record) {
        return (record.locationStatus != null) && record.locationStatus === 3;
      };
      $scope.getDistanceDisplay = function(distance) {
        if (distance != null) {
          return $filter('number')(distance * 1000, 0) + ' m';
        } else {
          return '';
        }
      };
      $scope.isNoOrder = function(record) {
        return !((record.hasOrder != null) && record.hasOrder);
      };
      $scope.isOrderApproved = function(record) {
        return !$scope.isNoOrder(record) && record.approveStatus === 1;
      };
      $scope.isOrderPending = function(record) {
        return !$scope.isNoOrder(record) && record.approveStatus === 0;
      };
      $scope.isOrderRejected = function(record) {
        return !$scope.isNoOrder(record) && record.approveStatus === 2;
      };
      $scope.goToDetail = function(record) {
        return $state.go('visit-detail', {
          id: record.id,
          filter: $stateParams.filter
        });
      };
      $scope.distributorFilterChange = function() {
        $scope.filter.salesmanId = null;
        return $scope.filterChange();
      };
      $scope.filterChange = function() {
        $scope.filter.currentPage = 1;
        return $scope.pageChange();
      };
      $scope.getReloadDataParams = function(params) {
        var summaryCallback, summaryLoadStatus, summaryParams;
        if (!$scope.mustSelectDistributor()) {
          LoadingUtilsService.loadSalesmenByDistributor($scope.who, $scope.filter.distributorId, $scope.loadStatus.getStatusByDataName('salesmen'), function(list) {
            return $scope.salesmen = _.union([
              {
                id: 'all',
                fullname: '-- ' + $filter('translate')('all') + ' --'
              }
            ], list);
          });
          params.category = 'visit';
          params.subCategory = 'today';
          if ($scope.isUseDistributorFilter()) {
            params.distributorId = $scope.filter.distributorId;
          }
          if (($scope.filter.salesmanId != null) && $scope.filter.salesmanId !== 'all') {
            params.salesmanId = $scope.filter.salesmanId;
          }
          summaryParams = angular.copy(params);
          summaryParams.param = 'summary';
          summaryLoadStatus = $scope.loadStatus.getStatusByDataName('summary');
          $scope.summary = {};
          summaryCallback = function(data) {
            return $scope.summary = data;
          };
          CtrlUtilsService.loadSingleData(summaryParams, summaryLoadStatus, summaryCallback, null);
          return params;
        }
      };
      CtrlInitiatorService.initPagingFilterViewCtrl($scope);
      $scope.addInitFunction(function() {
        var _ref;
        $scope.filter.salesmanId = (_ref = $scope.filter.salesmanId) != null ? _ref : 'all';
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

//# sourceMappingURL=visit-today-ctrl.js.map
