(function() {
  'use strict';
  angular.module('app').controller('ProductCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', function($scope, CtrlCategoryInitiatorService) {
      $scope.title = 'product.title';
      $scope.categoryName = 'product';
      $scope.usePopup = false;
      $scope.isUseDistributorFilter = function() {
        return false;
      };
      $scope.importState = 'import-product';
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
  ]).controller('ProductDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService) {
      $scope.title = 'product.title';
      $scope.categoryName = 'product';
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.defaultBackState = 'product';
      $scope.onLoadSuccess = function() {
        if ($scope.isNew() || $scope.isDraft()) {
          LoadingUtilsService.loadProductCategories($scope.who, $scope.loadStatus.getStatusByDataName('productCategories'), function(list) {
            return $scope.productCategories = list;
          });
          LoadingUtilsService.loadUOMs($scope.who, $scope.loadStatus.getStatusByDataName('uom'), function(list) {
            return $scope.uoms = list;
          });
        }
        if ($scope.isNew()) {
          $scope.record.productivity = 1;
        }
        if ($scope.record != null) {
          if ($scope.record.productCategory != null) {
            $scope.record.productCategoryId = $scope.record.productCategory.id;
          }
          if ($scope.record.uom != null) {
            return $scope.record.uomId = $scope.record.uom.id;
          }
        }
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=product-ctrl.js.map
