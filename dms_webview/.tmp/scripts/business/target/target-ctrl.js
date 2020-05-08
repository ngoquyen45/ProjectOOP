(function() {
  'use strict';
  angular.module('app').controller('TargetCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'target.title';
      $scope.afterFilterLoad = function() {};
      $scope.dateChange = function() {
        $scope.filter.date = globalUtils.createIsoDate($scope.date.date);
        return $scope.reloadForNewFilter();
      };
      $scope.goToDetail = function(record) {
        return $state.go('target-detail', {
          id: record.salesman.id,
          filter: $stateParams.filter
        });
      };
      CtrlInitiatorService.initFilterViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'target',
          month: $scope.date.date.getMonth(),
          year: $scope.date.date.getFullYear()
        };
        return CtrlUtilsService.loadListData(params, $scope.loadStatus.getStatusByDataName('data'), function(records) {
          return $scope.records = records;
        }, function(error) {
          if (error.status === 400) {
            return $state.go('404');
          } else {
            return toast.logError($filter('translate')('loading.error'));
          }
        });
      };
      $scope.addInitFunction(function() {
        if ($scope.filter.date != null) {
          $scope.date = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.date));
        } else {
          $scope.date = $scope.createDatePickerModel(new Date());
        }
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]).controller('TargetDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'target.title';
      $scope.defaultBackState = 'target';
      $scope.afterFilterLoad = function() {};
      $scope.checkBeforeSave = function() {
        if ($scope.isFormValid('form')) {
          return true;
        } else {
          toast.logError($filter('translate')('error.data.input.not.valid'));
          return false;
        }
      };
      $scope.getObjectToSave = function() {
        var recordForSave;
        recordForSave = angular.copy($scope.record);
        recordForSave.salesmanId = $stateParams.id;
        recordForSave.month = $scope.date.getMonth();
        recordForSave.year = $scope.date.getFullYear();
        return recordForSave;
      };
      $scope.getSaveParams = function(params) {
        params.category = 'target';
        return params;
      };
      $scope.onSaveSuccess = function() {
        return $scope.refresh();
      };
      $scope.onSaveFailure = function() {};
      $scope.isPost = function() {
        return false;
      };
      CtrlInitiatorService.initFilterViewCtrl($scope);
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      CtrlInitiatorService.initCanSaveViewCtrl($scope);
      $scope.getMonthDisplay = function() {
        return moment($scope.date).format('MM/YYYY');
      };
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'target',
          subCategory: 'detail',
          month: $scope.date.getMonth(),
          year: $scope.date.getFullYear(),
          salesmanId: $stateParams.id
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
        if ($stateParams.id != null) {
          $scope.date = $scope.filter.date != null ? globalUtils.parseIsoDate($scope.filter.date) : new Date();
          if ($scope.date != null) {
            return $scope.reloadData();
          } else {
            return $state.go('404');
          }
        } else {
          return $state.go('404');
        }
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=target-ctrl.js.map
