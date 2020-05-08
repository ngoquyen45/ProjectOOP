'use strict'

angular.module('app')

.controller('CalendarConfigCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService) ->
            $scope.title = 'calendar.config.title'
            $scope.categoryName  = 'calendar-config'
            $scope.isIdRequire = -> false

            # AFTER LOAD
            $scope.onLoadSuccess = ->
                workingDays = []
                if $scope.record.workingDays?
                    workingDays = $scope.record.workingDays

                $scope.record.sunday = _.includes(workingDays, 1)
                $scope.record.monday = _.includes(workingDays, 2)
                $scope.record.tuesday = _.includes(workingDays, 3)
                $scope.record.wednesday = _.includes(workingDays, 4)
                $scope.record.thursday = _.includes(workingDays, 5)
                $scope.record.friday = _.includes(workingDays, 6)
                $scope.record.saturday = _.includes(workingDays, 7)

                # CHANGE FROM ISO DATE STRING TO DATE
                if $scope.record.holidays?
                    holidayDates = []
                    holidayDates.push($scope.createDatePickerModel(globalUtils.parseIsoDate(holiday))) for holiday in $scope.record.holidays
                    $scope.record.holidayDates = holidayDates

            # GET OBJECT TO SAVE
            $scope.getObjectToSave = ->
                record = angular.copy($scope.record)

                workingDays = []
                workingDays.push(1) if $scope.record.sunday
                workingDays.push(2) if $scope.record.monday
                workingDays.push(3) if $scope.record.tuesday
                workingDays.push(4) if $scope.record.wednesday
                workingDays.push(5) if $scope.record.thursday
                workingDays.push(6) if $scope.record.friday
                workingDays.push(7) if $scope.record.saturday
                record.workingDays = workingDays

                # CHANGE FROM DATE TO ISO DATE STRING
                if $scope.record.holidayDates?
                    holidays = []
                    holidays.push(globalUtils.createIsoDate(holidayDate.date)) for holidayDate in $scope.record.holidayDates when holidayDate.date?
                    record.holidays = holidays

                return record

            $scope.holidaysEmpty = ->
                return not $scope.record? or not $scope.record.holidayDates? or $scope.record.holidayDates.length == 0

            $scope.addHoliday = ->
                $scope.changeStatus.markAsChanged()
                if not $scope.record.holidayDates?
                    $scope.record.holidayDates = []
                $scope.record.holidayDates.push($scope.createDatePickerModel(null))

            $scope.removeHoliday = (holidayDate) ->
                $scope.changeStatus.markAsChanged()
                if $scope.record.holidayDates?
                    $scope.record.holidayDates.splice(_.indexOf($scope.record.holidayDates, holidayDate), 1)
                    $scope.removeDatePickerModel(holidayDate)

            $scope.onOpenDatePicker = -> $scope.changeStatus.markAsChanged()

            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.init()
    ])

