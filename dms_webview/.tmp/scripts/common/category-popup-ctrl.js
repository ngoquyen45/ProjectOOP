(function() {
  'use strict';
  angular.module('app').controller('NameCategoryPopupCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', '$modalInstance', 'title', 'categoryName', 'id', 'useDistributorFilter', function($scope, CtrlCategoryInitiatorService, $modalInstance, title, categoryName, id, useDistributorFilter) {
      $scope.title = title;
      $scope.categoryName = categoryName;
      $scope.id = id;
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.isUseDistributorFilter = function() {
        return useDistributorFilter != null ? useDistributorFilter : false;
      };
      CtrlCategoryInitiatorService.initCategoryDetailPopupCtrl($scope, $modalInstance);
      return $scope.init();
    }
  ]).controller('NameCodeCategoryPopupCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', '$modalInstance', 'title', 'categoryName', 'id', 'useDistributorFilter', function($scope, CtrlCategoryInitiatorService, $modalInstance, title, categoryName, id, useDistributorFilter) {
      $scope.title = title;
      $scope.categoryName = categoryName;
      $scope.id = id;
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.isUseDistributorFilter = function() {
        return useDistributorFilter != null ? useDistributorFilter : false;
      };
      $scope.useCode = true;
      CtrlCategoryInitiatorService.initCategoryDetailPopupCtrl($scope, $modalInstance);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=category-popup-ctrl.js.map
