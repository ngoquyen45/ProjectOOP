(function() {
  'use strict';
  angular.module('app').controller('CustomerCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', function($scope, CtrlCategoryInitiatorService) {
      $scope.title = 'customer.title';
      $scope.categoryName = 'customer';
      $scope.usePopup = false;
      $scope.isUseDistributorFilter = function() {
        return $scope.who !== 'distributor';
      };
      $scope.importState = 'import-customer';
      if ($scope.isUseDistributorFilter()) {
        $scope.columns = [
          {
            header: 'name',
            property: 'name'
          }, {
            header: 'code',
            property: 'code'
          }, {
            header: 'distributor',
            property: function(record) {
              return record.distributor.name;
            }
          }, {
            header: 'area',
            property: function(record) {
              return record.area.name;
            }
          }, {
            header: 'customer.type',
            property: function(record) {
              return record.customerType.name;
            }
          }
        ];
      } else {
        $scope.columns = [
          {
            header: 'name',
            property: 'name'
          }, {
            header: 'code',
            property: 'code'
          }, {
            header: 'area',
            property: function(record) {
              return record.area.name;
            }
          }, {
            header: 'customer.type',
            property: function(record) {
              return record.customerType.name;
            }
          }
        ];
      }
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]).controller('CustomerDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService) {
      var reloadArea;
      $scope.title = 'customer.title';
      $scope.categoryName = 'customer';
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.defaultBackState = 'customer';
      $scope.useMap = true;
      $scope.getLocation = function() {
        return $scope.record.location;
      };
      $scope.onLoadSuccess = function(record) {
        if ($scope.isNew() || $scope.isDraft()) {
          LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            return $scope.distributors = list;
          });
          LoadingUtilsService.loadCustomerTypes($scope.who, $scope.loadStatus.getStatusByDataName('customerTypes'), function(list) {
            return $scope.customerTypes = list;
          });
        }
        if (record != null) {
          if ((record.distributor != null) && (record.distributor.id != null)) {
            $scope.record.distributorId = $scope.record.distributor.id;
            reloadArea();
          }
          if ((record.customerType != null) && (record.customerType.id != null)) {
            $scope.record.customerTypeId = $scope.record.customerType.id;
          }
          if ((record.area != null) && (record.area.id != null)) {
            return $scope.record.areaId = $scope.record.area.id;
          }
        }
      };
      reloadArea = function() {
        if (($scope.record != null) && ($scope.record.distributorId != null)) {
          return LoadingUtilsService.loadAreasByDistributor($scope.who, $scope.record.distributorId, $scope.loadStatus.getStatusByDataName('areas'), function(list) {
            return $scope.areas = list;
          });
        } else {
          return $scope.areas = [];
        }
      };
      $scope.changeDistributor = function() {
        $scope.record.areaId = null;
        $scope.changeStatus.markAsChanged();
        return reloadArea();
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=customer-ctrl.js.map
