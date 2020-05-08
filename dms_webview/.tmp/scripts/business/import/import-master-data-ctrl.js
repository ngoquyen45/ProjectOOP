(function() {
  'use strict';
  angular.module('app').controller('ImportMasterDataCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) {
      var importMasterData, initStep, verify;
      $scope.title = 'Import Master Data';
      $scope.defaultBackState = 'client-detail';
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
          if ($scope.currentStep === 0) {
            return $scope.global.excel === null;
          } else if ($scope.currentStep === 1) {
            return ($scope.global.confirm == null) || $scope.global.confirm.errorTemplate;
          }
        }
        return true;
      };
      $scope.nextStep = function() {
        if ($scope.canNext()) {
          if ($scope.currentStep === 0) {
            return verify();
          } else {
            return importMasterData();
          }
        }
      };
      $scope.canBack = function() {
        return ($scope.currentStep != null) && $scope.currentStep >= 0 && !$scope.isFinish();
      };
      $scope.backStep = function() {
        $scope.global.excel = null;
        if ($scope.currentStep === 0) {
          return $scope.back();
        } else {
          return $scope.currentStep--;
        }
      };
      $scope.isFinish = function() {
        return $scope.currentStep >= 2;
      };
      $scope.finish = function() {
        if ($scope.isFinish()) {
          return $scope.back();
        }
      };
      $scope.getTemplateLink = function() {
        return ADDRESS_BACKEND + $scope.who + '/import/master-data/template?' + 'access_token=' + $token.getAccessToken() + '&lang=' + CtrlUtilsService.getUserLanguage();
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
          subCategory: 'master-data',
          param: 'verify',
          fileId: $scope.global.excel.fileId
        };
        onSuccess = function(data) {
          var _i, _ref, _results;
          $scope.global.confirm = data;
          $scope.columnIndex = (function() {
            _results = [];
            for (var _i = 0, _ref = $scope.global.confirm.nbColumn - 1; 0 <= _ref ? _i <= _ref : _i >= _ref; 0 <= _ref ? _i++ : _i--){ _results.push(_i); }
            return _results;
          }).apply(this);
          return $scope.currentStep++;
        };
        return CtrlUtilsService.loadSingleData(params, status, onSuccess, null);
      };
      importMasterData = function() {
        var onSuccess, params, status;
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'import',
          subCategory: 'master-data',
          param: 'import',
          clientId: $stateParams.id,
          fileId: $scope.global.excel.fileId
        };
        onSuccess = function() {
          return $scope.currentStep++;
        };
        return CtrlUtilsService.doPost(params, status, null, onSuccess, null, 'save');
      };
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      $scope.back = function() {
        var backState, _ref;
        backState = (_ref = $stateParams.parent) != null ? _ref : $scope.defaultBackState;
        return $state.go(backState, {
          id: $stateParams.id,
          filter: $stateParams.filter
        });
      };
      $scope.addInitFunction(function() {
        if ($stateParams.id == null) {
          $state.go('404');
        }
        initStep();
        $scope.global = {};
        $scope.global.excel = null;
        return $scope.global.confirm = null;
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=import-master-data-ctrl.js.map
