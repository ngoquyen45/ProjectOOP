(function() {
  'use strict';
  angular.module('app').controller('ImportCustomerCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) {
      var importCustomer, initStep, verify;
      $scope.title = 'import.customer.title';
      $scope.defaultBackState = 'customer';
      initStep = function() {
        $scope.currentStep = 0;
        return $scope.steps = [
          {
            name: 'import.step.select.distributor'
          }, {
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
          if ($scope.currentStep === 0) {
            return $scope.global.distributorId === null;
          } else if ($scope.currentStep === 1) {
            return $scope.global.excel === null;
          } else if ($scope.currentStep === 2) {
            return false;
          }
        }
        return true;
      };
      $scope.nextStep = function() {
        var distributor, _i, _len, _ref;
        if ($scope.canNext()) {
          if ($scope.currentStep === 0) {
            _ref = $scope.distributors;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              distributor = _ref[_i];
              if (distributor.id === $scope.global.distributorId) {
                $scope.global.distributorName = distributor.name;
              }
            }
            return $scope.currentStep++;
          } else if ($scope.currentStep === 1) {
            return verify();
          } else if ($scope.currentStep === 2) {
            return importCustomer();
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
        return ADDRESS_BACKEND + $scope.who + '/import/customer/template?' + 'distributorId=' + $scope.global.distributorId + '&access_token=' + $token.getAccessToken() + '&lang=' + CtrlUtilsService.getUserLanguage();
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
          subCategory: 'customer',
          param: 'verify',
          distributorId: $scope.global.distributorId,
          fileId: $scope.global.excel.fileId
        };
        onSuccess = function(data) {
          $scope.global.confirm = data;
          return $scope.currentStep++;
        };
        return CtrlUtilsService.loadSingleData(params, status, onSuccess, null);
      };
      importCustomer = function() {
        var onSuccess, params, status;
        $scope.global.result = {};
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'import',
          subCategory: 'customer',
          param: 'import',
          distributorId: $scope.global.distributorId,
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
        LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
          return $scope.distributors = angular.copy(list);
        });
        initStep();
        $scope.global = {};
        $scope.global.distributorId = null;
        $scope.global.distributorName = null;
        $scope.global.excel = null;
        $scope.global.confirm = null;
        return $scope.global.result = null;
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=import-customer-ctrl.js.map
