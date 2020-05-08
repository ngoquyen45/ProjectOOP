(function() {
  'use strict';
  angular.module('app').controller('OrderExportCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) {
      var checkDate, checkDateDuration;
      $scope.title = 'order.export.title';
      $scope.isUseDistributorFilter = function() {
        return CtrlUtilsService.getWho() !== 'distributor';
      };
      $scope["export"] = function() {
        var href;
        if (($scope.filter.distributorId != null) || !$scope.isUseDistributorFilter()) {
          if (checkDate()) {
            if (checkDateDuration()) {
              $scope.filter.fromDate = globalUtils.createIsoDate($scope.fromDate.date);
              $scope.filter.toDate = globalUtils.createIsoDate($scope.toDate.date);
              href = ADDRESS_BACKEND + $scope.who + '/export/order';
              if ($scope.filter.isDetail) {
                href = href + '/detail';
              }
              href = href + '?access_token=' + $token.getAccessToken();
              href = href + '&distributorId=' + $scope.filter.distributorId;
              href = href + '&fromDate=' + $scope.filter.fromDate;
              href = href + '&toDate=' + $scope.filter.toDate;
              href = href + '&lang=' + CtrlUtilsService.getUserLanguage();
              return location.href = href;
            } else {
              return toast.logError($filter('translate')('max.duration.between.from.date.and.to.date.is.2.month'));
            }
          } else {
            return toast.logError($filter('translate')('from.date.cannot.be.greater.than.to.date'));
          }
        } else {
          return toast.logError($filter('translate')('please.select.distributor'));
        }
      };
      checkDate = function() {
        if (($scope.fromDate != null) && ($scope.fromDate.date != null) && $scope.toDate && ($scope.toDate.date != null)) {
          if ($scope.fromDate.date.getTime() <= $scope.toDate.date.getTime()) {
            return true;
          }
        }
        return false;
      };
      checkDateDuration = function() {
        if (moment($scope.fromDate.date).add(2, 'months').toDate().getTime() < $scope.toDate.date.getTime()) {
          return false;
        }
        return true;
      };
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      $scope.addInitFunction(function() {
        $scope.filter = {};
        $scope.filter.isDetail = false;
        $scope.filter.distributorId = null;
        $scope.fromDate = $scope.createDatePickerModel(new Date());
        $scope.toDate = $scope.createDatePickerModel(new Date());
        if ($scope.isUseDistributorFilter()) {
          return LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            return $scope.distributors = list;
          });
        }
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=order-export-ctrl.js.map
