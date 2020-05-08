(function() {
  'use strict';
  angular.module('app').controller('FeedbackCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'feedback.title';
      $scope.filterChange = function() {
        $scope.filter.currentPage = 1;
        return $scope.pageChange();
      };
      $scope.goToDetail = function(record) {
        return $state.go('feedback-detail', {
          id: record.id,
          filter: $stateParams.filter
        });
      };
      $scope.getFeedbackDisplay = function(feedbacks) {
        if ((feedbacks != null) && feedbacks.length > 0) {
          return feedbacks[0];
        } else {
          return 'N/A';
        }
      };
      $scope.getTimeDisplay = function(isoTime) {
        var date;
        date = globalUtils.parseIsoTime(isoTime);
        if ($scope.today.getFullYear() === date.getFullYear() && $scope.today.getMonth() === date.getMonth() && $scope.today.getDate() === date.getDate()) {
          return moment(date).format('HH:mm');
        } else {
          return moment(date).format(CtrlUtilsService.getUserInfo().dateFormat.toUpperCase());
        }
      };
      $scope.getReloadDataParams = function(params) {
        params.category = 'feedback';
        if ($scope.filter.orderForDP != null) {
          if ($scope.filter.orderForDP === 'normal') {
            params.orderForDP = false;
          } else if ($scope.filter.orderForDP === 'dp') {
            params.orderForDP = true;
          }
        }
        return params;
      };
      CtrlInitiatorService.initPagingFilterViewCtrl($scope);
      $scope.addInitFunction(function() {
        $scope.today = new Date();
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]).controller('FeedbackDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      $scope.title = 'feedback.title';
      $scope.defaultBackState = 'feedback';
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'feedback',
          id: $stateParams.id
        };
        return CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(record) {
          return $scope.record = record;
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
          $state.go('404');
        }
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=feedback-ctrl.js.map
