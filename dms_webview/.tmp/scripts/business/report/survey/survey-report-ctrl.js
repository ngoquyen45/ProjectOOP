(function() {
  'use strict';
  angular.module('app').controller('SurveyReportCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) {
      $scope.title = 'survey.report.title';
      $scope.goToReport = function(record) {
        return $state.go('survey-report-detail', {
          id: record.id,
          filter: $stateParams.filter
        });
      };
      $scope["export"] = function(record) {
        return location.href = ADDRESS_BACKEND + $scope.who + '/report/survey/' + record.id + '/export?access_token=' + $token.getAccessToken() + '&lang=' + CtrlUtilsService.getUserLanguage();
      };
      $scope.getReloadDataParams = function(params) {
        params.category = 'report';
        params.subCategory = 'survey';
        return params;
      };
      CtrlInitiatorService.initPagingFilterViewCtrl($scope);
      $scope.addInitFunction(function() {
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]).controller('SurveyReportDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      var afterLoad, pieOptions;
      $scope.title = 'survey.report.title';
      $scope.defaultBackState = 'survey-report';
      $scope.afterFilterLoad = function() {};
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      CtrlInitiatorService.initFilterViewCtrl($scope);
      pieOptions = {
        series: {
          pie: {
            show: true
          }
        },
        legend: {
          show: true
        },
        grid: {
          hoverable: true,
          clickable: false
        },
        colors: ["#60CD9B", "#66B5D7", "#EEC95A", "#E87352"],
        tooltip: true,
        tooltipOpts: {
          content: "%p.0%, %s",
          defaultTheme: false
        }
      };
      afterLoad = function() {
        var option, question, _i, _j, _len, _len1, _ref, _ref1, _results;
        if (($scope.record != null) && ($scope.record.questions != null)) {
          _ref = $scope.record.questions;
          _results = [];
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            question = _ref[_i];
            question.data = [];
            question.option = {};
            if (question.options != null) {
              _ref1 = question.options;
              for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
                option = _ref1[_j];
                question.data.push({
                  label: globalUtils.escapeHTML(option.name) + ' (' + option.result + ') ',
                  data: option.result
                });
              }
              _results.push(question.option = pieOptions);
            } else {
              _results.push(void 0);
            }
          }
          return _results;
        }
      };
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'report',
          subCategory: 'survey',
          id: $stateParams.id
        };
        return CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(data) {
          $scope.record = data;
          return afterLoad();
        }, function(error) {
          if (error.status === 400) {
            return $state.go('404');
          } else {
            return toast.logError($filter('translate')('loading.error'));
          }
        });
      };
      $scope.addInitFunction(function() {
        if ($stateParams.id == null) {
          return $state.go('404');
        } else {
          return $scope.reloadData();
        }
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=survey-report-ctrl.js.map
