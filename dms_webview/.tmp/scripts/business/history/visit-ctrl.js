(function() {
  'use strict';
  angular.module('app').controller('VisitCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      var checkDate, checkDateDuration;
      $scope.title = 'visit';
      $scope.isUseDistributorFilter = function() {
        return CtrlUtilsService.getWho() !== 'distributor';
      };
      $scope.search = function() {
        if (checkDate()) {
          if (checkDateDuration()) {
            $scope.filter.currentPage = 1;
            $scope.filter.fromDate = globalUtils.createIsoDate($scope.fromDate.date);
            $scope.filter.toDate = globalUtils.createIsoDate($scope.toDate.date);
            return $scope.pageChange();
          } else {
            return toast.logError($filter('translate')('max.duration.between.from.date.and.to.date.is.2.month'));
          }
        } else {
          return toast.logError($filter('translate')('from.date.cannot.be.greater.than.to.date'));
        }
      };
      checkDateDuration = function() {
        if (moment($scope.fromDate.date).add(2, 'months').toDate().getTime() < $scope.toDate.date.getTime()) {
          return false;
        }
        return true;
      };
      checkDate = function() {
        if (($scope.fromDate != null) && ($scope.fromDate.date != null) && $scope.toDate && ($scope.toDate.date != null)) {
          if ($scope.fromDate.date.getTime() <= $scope.toDate.date.getTime()) {
            return true;
          }
        }
        return false;
      };
      $scope.goToDetail = function(record) {
        return $state.go('visit-detail', {
          id: record.id,
          parent: 'visit',
          filter: $stateParams.filter
        });
      };
      $scope.changeDistributor = function() {
        if ($scope.isUseDistributorFilter()) {
          $scope.filter.salesmanId = 'all';
          if ($scope.isDisplaySalesmanFilter()) {
            return LoadingUtilsService.loadSalesmenByDistributor($scope.who, $scope.filter.distributorId, $scope.loadStatus.getStatusByDataName('salesmen'), function(list) {
              return $scope.salesmen = _.union([
                {
                  id: 'all',
                  fullname: '-- ' + $filter('translate')('all') + ' --'
                }
              ], list);
            });
          }
        }
      };
      $scope.isDisplaySalesmanFilter = function() {
        return !$scope.isUseDistributorFilter() || (($scope.filter.distributorId != null) && $scope.filter.distributorId !== 'all');
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
      $scope.getReloadDataParams = function(params) {
        params.category = 'visit';
        params.fromDate = globalUtils.createIsoDate($scope.fromDate.date);
        params.toDate = globalUtils.createIsoDate($scope.toDate.date);
        if ($scope.isUseDistributorFilter() && $scope.filter.distributorId !== 'all') {
          params.distributorId = $scope.filter.distributorId;
        }
        if (($scope.filter.salesmanId != null) && $scope.filter.salesmanId !== 'all') {
          params.salesmanId = $scope.filter.salesmanId;
        }
        return params;
      };
      CtrlInitiatorService.initPagingFilterViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      $scope.addInitFunction(function() {
        var distributorId, _ref, _ref1;
        if (($scope.filter.fromDate != null) && ($scope.filter.toDate != null)) {
          $scope.fromDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.fromDate));
          $scope.toDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.toDate));
          if (!checkDate() || !checkDateDuration()) {
            $state.go('404');
          }
        } else {
          $scope.fromDate = {
            date: new Date(),
            opened: false
          };
          $scope.toDate = {
            date: new Date(),
            opened: false
          };
        }
        $scope.filter.distributorId = (_ref = $scope.filter.distributorId) != null ? _ref : 'all';
        $scope.filter.salesmanId = (_ref1 = $scope.filter.salesmanId) != null ? _ref1 : 'all';
        if ($scope.isUseDistributorFilter()) {
          LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            return $scope.distributors = _.union([
              {
                id: 'all',
                name: '-- ' + $filter('translate')('all') + ' --'
              }
            ], list);
          });
        }
        if ($scope.isDisplaySalesmanFilter()) {
          distributorId = $scope.filter.distributorId !== 'all' ? $scope.filter.distributorId : null;
          LoadingUtilsService.loadSalesmenByDistributor($scope.who, distributorId, $scope.loadStatus.getStatusByDataName('salesmen'), function(list) {
            return $scope.salesmen = _.union([
              {
                id: 'all',
                fullname: '-- ' + $filter('translate')('all') + ' --'
              }
            ], list);
          });
        }
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=visit-ctrl.js.map
