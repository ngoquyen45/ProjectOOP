(function() {
  'use strict';
  angular.module('app').controller('DistributorPriceListCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'distributor.price.list.title';
      $scope.goToImport = function() {
        return $state.go('import-price-list', {
          filter: $stateParams.filter
        });
      };
      $scope.reloadData = function() {
        var params;
        $scope.datas = [];
        params = {
          who: $scope.who,
          category: 'price-list'
        };
        return CtrlUtilsService.loadListData(params, $scope.loadStatus.getStatusByDataName('data'), function(records) {
          var productCategory, productCategoryMap;
          productCategoryMap = _.groupBy(records, function(product) {
            return product.productCategory.name;
          });
          $scope.datas = [];
          for (productCategory in productCategoryMap) {
            $scope.datas.push({
              name: productCategory,
              products: productCategoryMap[productCategory]
            });
          }
          return $scope.datas = _.sortByOrder($scope.datas, ['name'], ['asc']);
        }, function() {
          return toast.logError($filter('translate')('loading.error'));
        });
      };
      $scope.isEmpty = function() {
        return _.isEmpty($scope.datas);
      };
      $scope.checkBeforeSave = function() {
        if ($scope.isFormValid('form')) {
          return true;
        } else {
          toast.logError($filter('translate')('error.data.input.not.valid'));
          return false;
        }
      };
      $scope.getObjectToSave = function() {
        var data, product, saveData, _i, _j, _len, _len1, _ref, _ref1;
        saveData = {
          priceList: {}
        };
        _ref = $scope.datas;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          data = _ref[_i];
          _ref1 = data.products;
          for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
            product = _ref1[_j];
            if (product.distributorPrice != null) {
              saveData.priceList[product.id] = product.distributorPrice;
            }
          }
        }
        return saveData;
      };
      $scope.getSaveParams = function(params) {
        params.category = 'price-list';
        return params;
      };
      $scope.onSaveSuccess = function() {};
      $scope.onSaveFailure = function() {};
      $scope.isPost = function() {
        return false;
      };
      CtrlInitiatorService.initCanSaveViewCtrl($scope);
      $scope.addInitFunction(function() {
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=price-list-ctrl.js.map
