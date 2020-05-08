(function() {
  'use strict';
  angular.module('app').controller('CalendarConfigCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService) {
      $scope.title = 'calendar.config.title';
      $scope.categoryName = 'calendar-config';
      $scope.isIdRequire = function() {
        return false;
      };
      $scope.onLoadSuccess = function() {
        var holiday, holidayDates, workingDays, _i, _len, _ref;
        workingDays = [];
        if ($scope.record.workingDays != null) {
          workingDays = $scope.record.workingDays;
        }
        $scope.record.sunday = _.includes(workingDays, 1);
        $scope.record.monday = _.includes(workingDays, 2);
        $scope.record.tuesday = _.includes(workingDays, 3);
        $scope.record.wednesday = _.includes(workingDays, 4);
        $scope.record.thursday = _.includes(workingDays, 5);
        $scope.record.friday = _.includes(workingDays, 6);
        $scope.record.saturday = _.includes(workingDays, 7);
        if ($scope.record.holidays != null) {
          holidayDates = [];
          _ref = $scope.record.holidays;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            holiday = _ref[_i];
            holidayDates.push($scope.createDatePickerModel(globalUtils.parseIsoDate(holiday)));
          }
          return $scope.record.holidayDates = holidayDates;
        }
      };
      $scope.getObjectToSave = function() {
        var holidayDate, holidays, record, workingDays, _i, _len, _ref;
        record = angular.copy($scope.record);
        workingDays = [];
        if ($scope.record.sunday) {
          workingDays.push(1);
        }
        if ($scope.record.monday) {
          workingDays.push(2);
        }
        if ($scope.record.tuesday) {
          workingDays.push(3);
        }
        if ($scope.record.wednesday) {
          workingDays.push(4);
        }
        if ($scope.record.thursday) {
          workingDays.push(5);
        }
        if ($scope.record.friday) {
          workingDays.push(6);
        }
        if ($scope.record.saturday) {
          workingDays.push(7);
        }
        record.workingDays = workingDays;
        if ($scope.record.holidayDates != null) {
          holidays = [];
          _ref = $scope.record.holidayDates;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            holidayDate = _ref[_i];
            if (holidayDate.date != null) {
              holidays.push(globalUtils.createIsoDate(holidayDate.date));
            }
          }
          record.holidays = holidays;
        }
        return record;
      };
      $scope.holidaysEmpty = function() {
        return ($scope.record == null) || ($scope.record.holidayDates == null) || $scope.record.holidayDates.length === 0;
      };
      $scope.addHoliday = function() {
        $scope.changeStatus.markAsChanged();
        if ($scope.record.holidayDates == null) {
          $scope.record.holidayDates = [];
        }
        return $scope.record.holidayDates.push($scope.createDatePickerModel(null));
      };
      $scope.removeHoliday = function(holidayDate) {
        $scope.changeStatus.markAsChanged();
        if ($scope.record.holidayDates != null) {
          $scope.record.holidayDates.splice(_.indexOf($scope.record.holidayDates, holidayDate), 1);
          return $scope.removeDatePickerModel(holidayDate);
        }
      };
      $scope.onOpenDatePicker = function() {
        return $scope.changeStatus.markAsChanged();
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=calendar-config-ctrl.js.map
