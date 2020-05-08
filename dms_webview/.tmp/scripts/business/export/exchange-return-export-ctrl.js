(function() {
  'use strict';
  angular.module('app').controller('ExchangeReturnExportCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) {
      var checkDate, checkDateDuration;
      $scope.title = 'exchange.return.export.title';
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
              href = ADDRESS_BACKEND + $scope.who + '/export/exchange-return';
              href = href + '?access_token=' + $token.getAccessToken();
              if (($scope.filter.distributorId != null) && $scope.filter.distributorId !== 'all') {
                href = href + '&distributorId=' + $scope.filter.distributorId;
              }
              href = href + '&fromDate=' + $scope.filter.fromDate;
              href = href + '&toDate=' + $scope.filter.toDate;
              href = href + '&lang=' + CtrlUtilsService.getUserLanguage();
              return location.href = href;
            } else {
              return toast.logError($filter('translate')('max.duration.between.from.date.and.to.date.is.1.month'));
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
        if (moment($scope.fromDate.date).add(1, 'months').toDate().getTime() < $scope.toDate.date.getTime()) {
          return false;
        }
        return true;
      };
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      $scope.addInitFunction(function() {
        $scope.filter = {};
        $scope.filter.distributorId = 'all';
        $scope.fromDate = $scope.createDatePickerModel(new Date());
        $scope.toDate = $scope.createDatePickerModel(new Date());
        if ($scope.isUseDistributorFilter()) {
          return LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            return $scope.distributors = _.union([
              {
                id: 'all',
                name: '-- ' + $filter('translate')('all') + ' --'
              }
            ], list);
          });
        }
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=exchange-return-export-ctrl.js.map
