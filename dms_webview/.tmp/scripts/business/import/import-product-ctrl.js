(function() {
  'use strict';
  angular.module('app').controller('ImportProductCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) {
      var getValidRows, importProduct, initStep, verify;
      $scope.title = 'import.product.title';
      $scope.defaultBackState = 'product';
      initStep = function() {
        $scope.currentStep = 0;
        return $scope.steps = [
          {
            name: 'import.step.upload.file'
          }, {
            name: 'import.step.confirm'
          }, {
            name: 'import.step.add.photo'
          }, {
            name: 'import.step.finalisation'
          }
        ];
      };
      $scope.isDoneStep = function(stepIndex) {
        return stepIndex < $scope.currentStep;
      };
      $scope.isCurrentStep = function(stepIndex) {
        return $scope.currentStep === stepIndex;
      };
      $scope.canNext = function() {
        return ($scope.currentStep != null) && $scope.currentStep >= 0 && !$scope.isFinish();
      };
      $scope.isNextDisabled = function() {
        if ($scope.canNext()) {
          if ($scope.currentStep === 0) {
            return $scope.global.excel === null;
          } else if ($scope.currentStep === 1) {
            return ($scope.global.confirm.rowDatas !== null && $scope.global.confirm.rowDatas.length >= $scope.global.confirm.total) || $scope.global.confirm.total <= 0;
          } else if ($scope.currentStep === 2) {
            return !$scope.isFormValid('form');
          }
        }
        return true;
      };
      $scope.nextStep = function() {
        if ($scope.canNext()) {
          if ($scope.currentStep === 0) {
            return verify();
          } else if ($scope.currentStep === 1) {
            return getValidRows();
          } else if ($scope.currentStep === 2) {
            return importProduct();
          }
        }
      };
      $scope.canBack = function() {
        return ($scope.currentStep != null) && $scope.currentStep >= 0 && !$scope.isFinish();
      };
      $scope.backStep = function() {
        if ($scope.currentStep === 0) {
          return $scope.back();
        } else {
          return $scope.currentStep--;
        }
      };
      $scope.isFinish = function() {
        return $scope.currentStep >= 3;
      };
      $scope.finish = function() {
        if ($scope.isFinish()) {
          return $scope.back();
        }
      };
      $scope.getTemplateLink = function() {
        return ADDRESS_BACKEND + $scope.who + '/import/product/template?' + 'access_token=' + $token.getAccessToken() + '&lang=' + CtrlUtilsService.getUserLanguage();
      };
      $scope.getRowErrorData = function(data) {
        if (_.isEmpty(data)) {
          return '<' + $filter('translate')('import.customer.data.empty') + '>';
        } else {
          return data;
        }
      };
      verify = function() {
        var onSuccess, params, status;
        $scope.global.confirm = {};
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'import',
          subCategory: 'product',
          param: 'verify',
          fileId: $scope.global.excel.fileId
        };
        onSuccess = function(data) {
          $scope.global.confirm = data;
          return $scope.currentStep++;
        };
        return CtrlUtilsService.loadSingleData(params, status, onSuccess, null);
      };
      getValidRows = function() {
        var onSuccess, params, status;
        $scope.global.data = {};
        $scope.global.photos = [];
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'import',
          subCategory: 'product',
          param: 'confirm',
          fileId: $scope.global.excel.fileId
        };
        onSuccess = function(data) {
          $scope.global.data = data;
          $scope.global.photos = new Array(data.rowDatas.length);
          return $scope.currentStep++;
        };
        return CtrlUtilsService.loadSingleData(params, status, onSuccess, null);
      };
      importProduct = function() {
        var onSuccess, params, status;
        $scope.global.result = {};
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'import',
          subCategory: 'product',
          param: 'import',
          fileId: $scope.global.excel.fileId
        };
        onSuccess = function(data) {
          $scope.global.result = data;
          return $scope.currentStep++;
        };
        return CtrlUtilsService.doPost(params, status, {
          photos: $scope.global.photos
        }, onSuccess, null, 'save');
      };
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      $scope.addInitFunction(function() {
        initStep();
        $scope.global = {};
        $scope.global.distributorId = null;
        $scope.global.distributorName = null;
        $scope.global.excel = null;
        return $scope.global.confirm = null;
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=import-product-ctrl.js.map
