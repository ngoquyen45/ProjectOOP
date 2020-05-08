(function() {
  'use strict';
  angular.module('app').controller('CustomerTypeCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', function($scope, CtrlCategoryInitiatorService) {
      $scope.title = 'customer.type.title';
      $scope.categoryName = 'customer-type';
      $scope.usePopup = true;
      $scope.isUseDistributorFilter = function() {
        return false;
      };
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=customer-type-ctrl.js.map
