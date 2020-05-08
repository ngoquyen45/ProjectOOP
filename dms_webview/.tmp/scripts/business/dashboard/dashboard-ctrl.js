(function() {
  'use strict';
  angular.module('app').controller('DashboardCtrl', [
    '$scope', '$state', '$stateParams', 'toast', '$filter', 'CtrlUtilsService', 'CtrlInitiatorService', function($scope, $state, $stateParams, toast, $filter, CtrlUtilsService, CtrlInitiatorService) {
      CtrlInitiatorService.initBasicViewCtrl($scope);
      $scope.getComparisonIcon = function(before, current) {
        if (before == null) {
          before = 0;
        }
        if (current == null) {
          current = 0;
        }
        if (before < current) {
          return 'fa-arrow-circle-up';
        } else if (before > current) {
          return 'fa-arrow-circle-down';
        } else if (before === current) {
          return 'fa-arrow-circle-right';
        }
      };
      $scope.isDisplayProgressWarning = function() {
        return ($scope.record != null) && ($scope.record.progressWarnings != null) && $scope.record.progressWarnings.length > 0;
      };
      $scope.isDisplayBestSellers = function() {
        return ($scope.record != null) && ($scope.record.bestSellers != null) && $scope.record.bestSellers.length > 0;
      };
      $scope.reloadData = function() {
        var callbackFailure, callbackSuccess, params, status;
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'dashboard'
        };
        callbackSuccess = function(data) {
          return $scope.record = data;
        };
        callbackFailure = function() {
          return toast.logError($filter('translate')('loading.error'));
        };
        return CtrlUtilsService.loadSingleData(params, status, callbackSuccess, callbackFailure);
      };
      $scope.addInitFunction(function() {
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=dashboard-ctrl.js.map
