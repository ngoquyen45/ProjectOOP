(function() {
  'use strict';
  angular.module('app').controller('ClientCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', function($scope, CtrlCategoryInitiatorService) {
      $scope.title = 'client.title';
      $scope.categoryName = 'client';
      $scope.usePopup = false;
      $scope.isUseDistributorFilter = function() {
        return false;
      };
      $scope.columns = [
        {
          header: 'name',
          property: 'name'
        }, {
          header: 'code',
          property: 'code'
        }
      ];
      $scope.useDeleteButton = false;
      $scope.useEnableButton = false;
      $scope.useActivateButton = false;
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]).controller('ClientDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, $state, $stateParams) {
      $scope.title = 'client.title';
      $scope.categoryName = 'client';
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.defaultBackState = 'client';
      $scope.goToImport = function() {
        return $state.go('import-master-data', {
          id: $stateParams.id,
          filter: $stateParams.filter
        });
      };
      $scope.useMap = true;
      $scope.getLocation = function() {
        if ($scope.record.clientConfig != null) {
          return $scope.record.clientConfig.location;
        } else {
          return null;
        }
      };
      $scope.onLoadSuccess = function() {
        var holiday, holidayDates, workingDays, _i, _len, _ref, _ref1, _ref2;
        $scope.record.clientConfig = (_ref = $scope.record.clientConfig) != null ? _ref : {};
        if ($scope.record.clientConfig.visitDurationKPI != null) {
          $scope.record.clientConfig.visitDurationKPIinMinute = $scope.record.clientConfig.visitDurationKPI / 60 / 1000;
        }
        $scope.record.calendarConfig = (_ref1 = $scope.record.calendarConfig) != null ? _ref1 : {};
        workingDays = [];
        if ($scope.record.calendarConfig.workingDays != null) {
          workingDays = $scope.record.calendarConfig.workingDays;
        }
        $scope.record.calendarConfig.sunday = _.includes(workingDays, 1);
        $scope.record.calendarConfig.monday = _.includes(workingDays, 2);
        $scope.record.calendarConfig.tuesday = _.includes(workingDays, 3);
        $scope.record.calendarConfig.wednesday = _.includes(workingDays, 4);
        $scope.record.calendarConfig.thursday = _.includes(workingDays, 5);
        $scope.record.calendarConfig.friday = _.includes(workingDays, 6);
        $scope.record.calendarConfig.saturday = _.includes(workingDays, 7);
        if ($scope.record.calendarConfig.holidays != null) {
          holidayDates = [];
          _ref2 = $scope.record.calendarConfig.holidays;
          for (_i = 0, _len = _ref2.length; _i < _len; _i++) {
            holiday = _ref2[_i];
            holidayDates.push($scope.createDatePickerModel(globalUtils.parseIsoDate(holiday)));
          }
          return $scope.record.calendarConfig.holidayDates = holidayDates;
        }
      };
      $scope.getObjectToSave = function() {
        var holidayDate, holidays, record, workingDays, _i, _len, _ref;
        record = angular.copy($scope.record);
        record.clientConfig = angular.copy($scope.record.clientConfig);
        record.calendarConfig = angular.copy($scope.record.calendarConfig);
        record.clientConfig.visitDurationKPI = $scope.record.clientConfig.visitDurationKPIinMinute * 60 * 1000;
        workingDays = [];
        if ($scope.record.calendarConfig.sunday) {
          workingDays.push(1);
        }
        if ($scope.record.calendarConfig.monday) {
          workingDays.push(2);
        }
        if ($scope.record.calendarConfig.tuesday) {
          workingDays.push(3);
        }
        if ($scope.record.calendarConfig.wednesday) {
          workingDays.push(4);
        }
        if ($scope.record.calendarConfig.thursday) {
          workingDays.push(5);
        }
        if ($scope.record.calendarConfig.friday) {
          workingDays.push(6);
        }
        if ($scope.record.calendarConfig.saturday) {
          workingDays.push(7);
        }
        record.calendarConfig.workingDays = workingDays;
        if (record.calendarConfig.holidayDates != null) {
          holidays = [];
          _ref = $scope.record.calendarConfig.holidayDates;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            holidayDate = _ref[_i];
            if (holidayDate.date != null) {
              holidays.push(globalUtils.createIsoDate(holidayDate.date));
            }
          }
          record.calendarConfig.holidays = holidays;
        }
        return record;
      };
      $scope.holidaysEmpty = function() {
        return ($scope.record == null) || ($scope.record.calendarConfig == null) || ($scope.record.calendarConfig.holidayDates == null) || $scope.record.calendarConfig.holidayDates.length === 0;
      };
      $scope.addHoliday = function() {
        $scope.changeStatus.markAsChanged();
        if ($scope.record.calendarConfig.holidayDates == null) {
          $scope.record.calendarConfig.holidayDates = [];
        }
        return $scope.record.calendarConfig.holidayDates.push($scope.createDatePickerModel(null));
      };
      $scope.removeHoliday = function(holidayDate) {
        $scope.changeStatus.markAsChanged();
        if ($scope.record.calendarConfig.holidayDates != null) {
          $scope.record.calendarConfig.holidayDates.splice(_.indexOf($scope.record.calendarConfig.holidayDates, holidayDate), 1);
          return $scope.removeDatePickerModel(holidayDate);
        }
      };
      $scope.onOpenDatePicker = function() {
        return $scope.changeStatus.markAsChanged();
      };
      $scope.createSampleMasterData = function() {
        var params, status;
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'client',
          action: 'create-sample-master-data',
          clientId: $stateParams.id
        };
        return CtrlUtilsService.doPost(params, status, null, null, null, 'save');
      };
      $scope.generateVisitAndOrder = function() {
        var params, status;
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: 'client',
          action: 'generate-visit-and-order',
          clientId: $stateParams.id
        };
        return CtrlUtilsService.doPost(params, status, null, null, null, 'save');
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=client-ctrl.js.map
