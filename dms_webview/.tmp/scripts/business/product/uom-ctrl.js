(function() {
  'use strict';
  angular.module('app').controller('UomCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', function($scope, CtrlCategoryInitiatorService) {
      $scope.title = 'uom.title';
      $scope.categoryName = 'uom';
      $scope.usePopup = true;
      $scope.isUseDistributorFilter = function() {
        return false;
      };
      $scope.popupCtrl = 'NameCodeCategoryPopupCtrl';
      $scope.columns = [
        {
          header: 'name',
          property: 'name'
        }, {
          header: 'code',
          property: 'code'
        }
      ];
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=uom-ctrl.js.map
