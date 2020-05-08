(function() {
  'use strict';
  angular.module('app').controller('AreaCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', function($scope, CtrlCategoryInitiatorService) {
      $scope.title = 'area.title';
      $scope.categoryName = 'area';
      $scope.usePopup = true;
      $scope.isUseDistributorFilter = function() {
        return true;
      };
      $scope.columns = [
        {
          header: 'name',
          property: 'name'
        }, {
          header: 'distributor',
          property: function(record) {
            return record.distributor.name;
          }
        }
      ];
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=area-ctrl.js.map
