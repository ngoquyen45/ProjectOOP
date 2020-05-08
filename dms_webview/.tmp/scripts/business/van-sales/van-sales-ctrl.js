(function() {
  'use strict';
  angular.module('app').controller('VanSalesCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'van.sales.title';
      $scope.reloadData = function() {
        var params;
        $scope.records = [];
        params = {
          who: $scope.who,
          category: 'van-sales'
        };
        return CtrlUtilsService.loadListData(params, $scope.loadStatus.getStatusByDataName('data'), function(records) {
          return $scope.records = records;
        }, function() {
          return toast.logError($filter('translate')('loading.error'));
        });
      };
      $scope.isEmpty = function() {
        return _.isEmpty($scope.records);
      };
      $scope.checkBeforeSave = function() {
        return true;
      };
      $scope.getObjectToSave = function() {
        var record, saveData, _i, _len, _ref;
        saveData = {};
        _ref = $scope.records;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          record = _ref[_i];
          saveData[record.id] = record.vanSales;
        }
        return saveData;
      };
      $scope.getSaveParams = function(params) {
        params.category = 'van-sales';
        return params;
      };
      $scope.onSaveSuccess = function() {};
      $scope.onSaveFailure = function() {};
      $scope.isPost = function() {
        return false;
      };
      CtrlInitiatorService.initCanSaveViewCtrl($scope);
      $scope.addInitFunction(function() {
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=van-sales-ctrl.js.map
