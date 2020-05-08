(function() {
  'use strict';
  angular.module('app').controller('SurveyCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', '$filter', function($scope, CtrlCategoryInitiatorService, $filter) {
      $scope.title = 'survey.title';
      $scope.categoryName = 'survey';
      $scope.usePopup = false;
      $scope.isUseDistributorFilter = function() {
        return false;
      };
      $scope.useActivateButton = false;
      $scope.columns = [
        {
          header: 'name',
          property: 'name'
        }, {
          header: 'start.date',
          property: function(record) {
            return $filter('isoDate')(record.startDate);
          }
        }, {
          header: 'end.date',
          property: function(record) {
            return $filter('isoDate')(record.endDate);
          }
        }
      ];
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]).controller('SurveyDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', '$filter', 'LoadingUtilsService', 'ADDRESS_BACKEND', 'toast', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, $filter, LoadingUtilsService, ADDRESS_BACKEND, toast) {
      $scope.title = 'survey.title';
      $scope.categoryName = 'survey';
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.defaultBackState = 'survey';
      $scope.onLoadSuccess = function() {
        $scope.startDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.record.startDate));
        return $scope.endDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.record.endDate));
      };
      $scope.checkBeforeSave = function() {
        if ($scope.startDate.date.getTime() > $scope.endDate.date.getTime()) {
          toast.logError($filter('translate')('error.start.date.after.end.date'));
          return false;
        }
        if (!$scope.hasQuestion()) {
          toast.logError($filter('translate')('error.data.input.not.valid'));
          return false;
        }
        return true;
      };
      $scope.getObjectToSave = function() {
        var record;
        record = angular.copy($scope.record);
        record.startDate = globalUtils.createIsoDate($scope.startDate.date);
        record.endDate = globalUtils.createIsoDate($scope.endDate.date);
        return record;
      };
      $scope.hasQuestion = function() {
        return ($scope.record != null) && ($scope.record.questions != null) && $scope.record.questions.length > 0;
      };
      $scope.addNewQuestion = function() {
        $scope.changeStatus.markAsChanged();
        return $scope.currentEdit = {
          index: null,
          question: {}
        };
      };
      $scope.removeQuestion = function(index) {
        $scope.changeStatus.markAsChanged();
        return $scope.record.questions.splice(index, 1);
      };
      $scope.editQuestion = function(index) {
        var option, question, questionEdit, _i, _len, _ref;
        $scope.changeStatus.markAsChanged();
        question = $scope.record.questions[index];
        questionEdit = angular.copy(question);
        if (question.options != null) {
          questionEdit.options = [];
          _ref = question.options;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            option = _ref[_i];
            questionEdit.options.push(angular.copy(option));
          }
        }
        return $scope.currentEdit = {
          index: index,
          question: questionEdit
        };
      };
      $scope.isDetailMode = function() {
        return $scope.currentEdit != null;
      };
      $scope.exitDetailMode = function() {
        return $scope.currentEdit = null;
      };
      $scope.applyEdit = function() {
        if (!$scope.isFormValid('form2')) {
          return toast.logError($filter('translate')('error.data.input.not.valid'));
        } else {
          if (($scope.currentEdit.question.options == null) || $scope.currentEdit.question.options.length < 2) {
            return toast.logError($filter('translate')('survey.at.least.2.option'));
          } else {
            if ($scope.currentEdit.index != null) {
              $scope.record.questions.splice($scope.currentEdit.index, 1, $scope.currentEdit.question);
            } else {
              if ($scope.record.questions == null) {
                $scope.record.questions = [];
              }
              $scope.record.questions.push($scope.currentEdit.question);
            }
            return $scope.exitDetailMode();
          }
        }
      };
      $scope.hasOption = function() {
        return ($scope.currentEdit != null) && ($scope.currentEdit.question != null) && ($scope.currentEdit.question.options != null) && $scope.currentEdit.question.options.length > 0;
      };
      $scope.addNewOption = function() {
        if ($scope.currentEdit.question.options == null) {
          $scope.currentEdit.question.options = [];
        }
        return $scope.currentEdit.question.options.push({});
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=survey-ctrl.js.map
