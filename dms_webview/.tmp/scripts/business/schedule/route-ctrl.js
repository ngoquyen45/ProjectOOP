(function() {
  'use strict';
  angular.module('app').controller('RouteCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', function($scope, CtrlCategoryInitiatorService) {
      $scope.title = 'route.title';
      $scope.categoryName = 'route';
      $scope.usePopup = false;
      $scope.isUseDistributorFilter = function() {
        return $scope.who !== 'distributor';
      };
      if ($scope.isUseDistributorFilter()) {
        $scope.columns = [
          {
            header: 'name',
            property: 'name'
          }, {
            header: 'distributor',
            property: function(record) {
              return record.distributor.name;
            }
          }, {
            header: 'salesman',
            property: function(record) {
              if (record.salesman != null) {
                return record.salesman.fullname;
              } else {
                return '';
              }
            }
          }
        ];
      } else {
        $scope.columns = [
          {
            header: 'name',
            property: 'name'
          }, {
            header: 'salesman',
            property: function(record) {
              if (record.salesman != null) {
                return record.salesman.fullname;
              } else {
                return '';
              }
            }
          }
        ];
      }
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]).controller('RouteDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter) {
      var reloadSalesmen;
      $scope.title = 'route.title';
      $scope.categoryName = 'route';
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.defaultBackState = 'route';
      $scope.isUseDistributorFilter = function() {
        return $scope.who !== 'distributor';
      };
      $scope.onLoadSuccess = function() {
        if ($scope.record != null) {
          if ($scope.isDraft()) {
            LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
              return $scope.distributors = list;
            });
          }
          if (($scope.record.distributor != null) && ($scope.record.distributor.id != null)) {
            $scope.record.distributorId = $scope.record.distributor.id;
            reloadSalesmen();
          }
          if (($scope.record.salesman != null) && ($scope.record.salesman.id != null)) {
            return $scope.record.salesmanId = $scope.record.salesman.id;
          }
        }
      };
      reloadSalesmen = function() {
        if (($scope.record != null) && ($scope.record.distributorId != null)) {
          return LoadingUtilsService.loadSalesmenByDistributor($scope.who, $scope.record.distributorId, $scope.loadStatus.getStatusByDataName('salesmen'), function(list) {
            return $scope.salesmen = _.union([
              {
                id: null,
                fullname: '-- ' + $filter('translate')('none') + ' --'
              }
            ], list);
          });
        } else {
          return $scope.salesmen = [];
        }
      };
      $scope.changeDistributor = function() {
        $scope.record.salesmanId = null;
        $scope.changeStatus.markAsChanged();
        return reloadSalesmen();
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=route-ctrl.js.map
