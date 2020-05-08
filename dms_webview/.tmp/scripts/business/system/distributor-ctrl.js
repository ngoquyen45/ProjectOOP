(function() {
  'use strict';
  angular.module('app').controller('DistributorCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', '$filter', function($scope, CtrlCategoryInitiatorService, $filter) {
      $scope.title = 'distributor.title';
      $scope.categoryName = 'distributor';
      $scope.usePopup = false;
      $scope.isUseDistributorFilter = function() {
        return false;
      };
      $scope.useActivateButton = false;
      $scope.columns = [
        {
          header: 'name',
          property: 'name'
        }, {
          header: 'code',
          property: 'code'
        }, {
          header: 'supervisor',
          property: function(record) {
            return record.supervisor.fullname;
          }
        }
      ];
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]).controller('DistributorDetailCtrl', [
    '$scope', '$stateParams', '$filter', 'CtrlUtilsService', 'LoadingUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', function($scope, $stateParams, $filter, CtrlUtilsService, LoadingUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService) {
      $scope.title = 'distributor.title';
      $scope.categoryName = 'distributor';
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.defaultBackState = 'distributor';
      $scope.onLoadSuccess = function() {
        if ($scope.record.supervisor != null) {
          return $scope.record.supervisorId = $scope.record.supervisor.id;
        }
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      $scope.addInitFunction(function() {
        return LoadingUtilsService.loadSupervisors($scope.who, $scope.loadStatus.getStatusByDataName('supervisor'), function(list) {
          return $scope.supervisors = list;
        });
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=distributor-ctrl.js.map
