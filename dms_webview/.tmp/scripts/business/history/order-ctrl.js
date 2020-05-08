(function() {
  'use strict';
  angular.module('app').controller('OrderCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      var checkDate, checkDateDuration;
      $scope.title = 'order';
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
      $scope.goToDetail = function(record) {
        return $state.go('order-detail', {
          id: record.id,
          filter: $stateParams.filter
        });
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
      $scope.getReloadDataParams = function(params) {
        params.category = 'order';
        params.fromDate = globalUtils.createIsoDate($scope.fromDate.date);
        params.toDate = globalUtils.createIsoDate($scope.toDate.date);
        if ($scope.isUseDistributorFilter() && $scope.filter.distributorId !== 'all') {
          params.distributorId = $scope.filter.distributorId;
        }
        return params;
      };
      CtrlInitiatorService.initPagingFilterViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      $scope.addInitFunction(function() {
        if (($scope.filter.fromDate != null) && ($scope.filter.toDate != null)) {
          $scope.fromDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.fromDate));
          $scope.toDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.toDate));
          if (!checkDate() || !checkDateDuration()) {
            $state.go('404');
          }
        } else {
          $scope.fromDate = $scope.createDatePickerModel(new Date());
          $scope.toDate = $scope.createDatePickerModel(new Date());
        }
        if ($scope.isUseDistributorFilter()) {
          LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            return $scope.distributors = _.union([
              {
                id: null,
                name: '-- ' + $filter('translate')('all') + ' --'
              }
            ], list);
          });
        }
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]).controller('OrderDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'order.approval.title';
      $scope.defaultBackState = 'order';
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      $scope.printOrder = function() {
        return CtrlUtilsService.printOrder('order');
      };
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'order',
          id: $stateParams.id
        };
        return CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(record) {
          return $scope.record = record;
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
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=order-ctrl.js.map
