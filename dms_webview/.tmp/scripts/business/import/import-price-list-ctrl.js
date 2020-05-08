(function() {
  'use strict';
  angular.module('app').controller('ImportDistributorPriceListCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) {
      var doImport, initStep, verify;
      $scope.title = 'import.distributor.price.list.title';
      $scope.defaultBackState = 'distributor-price-list';
      initStep = function() {
        $scope.currentStep = 0;
        return $scope.steps = [
          {
            name: 'import.step.upload.file'
          }, {
            name: 'import.step.confirm'
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
          if ($scope.currentStep === ($scope.steps.length - 3)) {
            return $scope.global.excel === null;
          } else if ($scope.currentStep === ($scope.steps.length - 2)) {
            return false;
          }
        }
        return true;
      };
      $scope.nextStep = function() {
        if ($scope.canNext()) {
          if ($scope.currentStep === ($scope.steps.length - 3)) {
            return verify();
          } else if ($scope.currentStep === ($scope.steps.length - 2)) {
            return doImport();
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
        return $scope.currentStep >= ($scope.steps.length - 1);
      };
      $scope.finish = function() {
        if ($scope.isFinish()) {
          return $scope.back();
        }
      };
      $scope.getTemplateLink = function() {
        return ADDRESS_BACKEND + $scope.who + '/import/price-list/template?' + 'access_token=' + $token.getAccessToken() + '&lang=' + CtrlUtilsService.getUserLanguage();
      };
      $scope.getRowErrorData = function(data) {
        if (_.isEmpty(data)) {
          return '<' + $filter('translate')('import.data.empty') + '>';
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
          subCategory: 'price-list',
          param: 'verify',
          fileId: $scope.global.excel.fileId
        };
        onSuccess = function(data) {
          $scope.global.confirm = data;
          return $scope.currentStep++;
        };
        return CtrlUtilsService.loadSingleData(params, status, onSuccess, null);
      };
      doImport = function() {
        var onSuccess, params, status;
        $scope.global.result = {};
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'import',
          subCategory: 'price-list',
          param: 'import',
          fileId: $scope.global.excel.fileId
        };
        onSuccess = function(data) {
          $scope.global.result = data;
          return $scope.currentStep++;
        };
        return CtrlUtilsService.doPost(params, status, null, onSuccess, null, 'save');
      };
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      $scope.addInitFunction(function() {
        initStep();
        $scope.global = {};
        $scope.global.excel = null;
        $scope.global.confirm = null;
        return $scope.global.result = null;
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=import-price-list-ctrl.js.map
