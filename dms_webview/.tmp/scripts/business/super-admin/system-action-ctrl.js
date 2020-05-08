(function() {
  angular.module('app').controller('SystemActionCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', function($scope, CtrlUtilsService, CtrlInitiatorService) {
      $scope.title = 'system.action.title';
      CtrlInitiatorService.initBasicViewCtrl($scope);
      $scope.resetCache = function() {
        var params, status;
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'system',
          action: 'reset-cache'
        };
        return CtrlUtilsService.doPut(params, status, null, null, null, 'reset.cache');
      };
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=system-action-ctrl.js.map
