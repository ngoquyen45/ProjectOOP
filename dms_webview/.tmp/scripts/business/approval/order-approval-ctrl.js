(function() {
  'use strict';
  angular.module('app').controller('OrderPendingCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'order.approval.title';
      $scope.filterChange = function() {
        $scope.filter.currentPage = 1;
        return $scope.pageChange();
      };
      $scope.goToDetail = function(record) {
        return $state.go('order-approval', {
          id: record.id,
          filter: $stateParams.filter
        });
      };
      $scope.getReloadDataParams = function(params) {
        params.category = 'order';
        params.subCategory = 'pending';
        return params;
      };
      CtrlInitiatorService.initPagingFilterViewCtrl($scope);
      $scope.addInitFunction(function() {
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]).controller('OrderApprovalCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      var doAction;
      $scope.title = 'order.approval.title';
      $scope.defaultBackState = 'order-pending';
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      $scope.printOrder = function() {
        return CtrlUtilsService.printOrder('order');
      };
      doAction = function(action, callbackSuccess, callbackFailure) {
        var params, status;
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'order',
          subCategory: 'pending',
          id: $stateParams.id,
          action: action
        };
        return CtrlUtilsService.doPut(params, status, null, callbackSuccess, callbackFailure, action);
      };
      $scope.approve = function() {
        return doAction('approve', function() {
          return $state.go('order-detail', {
            id: $scope.record.id,
            parent: 'order-pending',
            filter: $stateParams.filter
          });
        });
      };
      $scope.reject = function() {
        return doAction('reject', function() {
          return $scope.back();
        });
      };
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'order',
          subCategory: 'pending',
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

//# sourceMappingURL=order-approval-ctrl.js.map
