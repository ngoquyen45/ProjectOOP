(function() {
  'use strict';
  angular.module('app').controller('ProductCategoryCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', function($scope, CtrlCategoryInitiatorService) {
      $scope.title = 'product.category.title';
      $scope.categoryName = 'product-category';
      $scope.usePopup = true;
      $scope.isUseDistributorFilter = function() {
        return false;
      };
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=product-category-ctrl.js.map
