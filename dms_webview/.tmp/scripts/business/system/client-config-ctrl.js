(function() {
  'use strict';
  angular.module('app').controller('ClientConfigCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService) {
      $scope.title = 'client.config.title';
      $scope.categoryName = 'client-config';
      $scope.isIdRequire = function() {
        return false;
      };
      $scope.onLoadSuccess = function() {
        if ($scope.record.visitDurationKPI != null) {
          return $scope.record.visitDurationKPIinMinute = $scope.record.visitDurationKPI / 60 / 1000;
        }
      };
      $scope.getObjectToSave = function() {
        var record;
        record = angular.copy($scope.record);
        record.visitDurationKPI = $scope.record.visitDurationKPIinMinute * 60 * 1000;
        console.log(record);
        return record;
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=client-config-ctrl.js.map
