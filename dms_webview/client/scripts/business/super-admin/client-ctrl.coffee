'use strict'

angular.module('app')

.controller('ClientCtrl', [
        '$scope', 'CtrlCategoryInitiatorService'
        ($scope, CtrlCategoryInitiatorService) ->
            $scope.title = 'client.title'
            $scope.categoryName = 'client'
            $scope.usePopup = false
            $scope.isUseDistributorFilter = -> false

            $scope.columns = [
                { header: 'name', property: 'name' }
                { header: 'code', property: 'code' }
            ]

            $scope.useDeleteButton = false
            $scope.useEnableButton = false
            $scope.useActivateButton = false

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
    ])

.controller('ClientDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, $state, $stateParams) ->
            $scope.title = 'client.title'
            $scope.categoryName  = 'client'
            $scope.isIdRequire = -> true
            $scope.defaultBackState = 'client'

            $scope.goToImport = ->  $state.go('import-master-data', { id: $stateParams.id, filter: $stateParams.filter })

            $scope.useMap = true
            $scope.getLocation = -> if $scope.record.clientConfig? then $scope.record.clientConfig.location else null

            # AFTER LOAD
            $scope.onLoadSuccess = ->
                # CLIENT CONFIG
                $scope.record.clientConfig = $scope.record.clientConfig ? {}

                if $scope.record.clientConfig.visitDurationKPI?
                    $scope.record.clientConfig.visitDurationKPIinMinute = $scope.record.clientConfig.visitDurationKPI / 60 / 1000

                # CALENDAR CONFIG
                $scope.record.calendarConfig = $scope.record.calendarConfig ? {}
                workingDays = []
                if $scope.record.calendarConfig.workingDays?
                    workingDays = $scope.record.calendarConfig.workingDays

                $scope.record.calendarConfig.sunday = _.includes(workingDays, 1)
                $scope.record.calendarConfig.monday = _.includes(workingDays, 2)
                $scope.record.calendarConfig.tuesday = _.includes(workingDays, 3)
                $scope.record.calendarConfig.wednesday = _.includes(workingDays, 4)
                $scope.record.calendarConfig.thursday = _.includes(workingDays, 5)
                $scope.record.calendarConfig.friday = _.includes(workingDays, 6)
                $scope.record.calendarConfig.saturday = _.includes(workingDays, 7)

                # CHANGE FROM ISO DATE STRING TO DATE
                if $scope.record.calendarConfig.holidays?
                    holidayDates = []
                    holidayDates.push($scope.createDatePickerModel(globalUtils.parseIsoDate(holiday))) for holiday in $scope.record.calendarConfig.holidays
                    $scope.record.calendarConfig.holidayDates = holidayDates

            # GET OBJECT TO SAVE
            $scope.getObjectToSave = ->
                record = angular.copy($scope.record)
                record.clientConfig  = angular.copy($scope.record.clientConfig)
                record.calendarConfig  = angular.copy($scope.record.calendarConfig)

                # CLIENT CONFIG
                record.clientConfig.visitDurationKPI = $scope.record.clientConfig.visitDurationKPIinMinute * 60 * 1000

                # CALENDAR CONFIG
                workingDays = []
                workingDays.push(1) if $scope.record.calendarConfig.sunday
                workingDays.push(2) if $scope.record.calendarConfig.monday
                workingDays.push(3) if $scope.record.calendarConfig.tuesday
                workingDays.push(4) if $scope.record.calendarConfig.wednesday
                workingDays.push(5) if $scope.record.calendarConfig.thursday
                workingDays.push(6) if $scope.record.calendarConfig.friday
                workingDays.push(7) if $scope.record.calendarConfig.saturday
                record.calendarConfig.workingDays = workingDays

                # CHANGE FROM DATE TO ISO DATE STRING
                if record.calendarConfig.holidayDates?
                    holidays = []
                    holidays.push(globalUtils.createIsoDate(holidayDate.date)) for holidayDate in $scope.record.calendarConfig.holidayDates when holidayDate.date?
                    record.calendarConfig.holidays = holidays

                return record

            $scope.holidaysEmpty = ->
                return not $scope.record? or not $scope.record.calendarConfig? or not $scope.record.calendarConfig.holidayDates? or $scope.record.calendarConfig.holidayDates.length == 0

            $scope.addHoliday = ->
                $scope.changeStatus.markAsChanged()
                if not $scope.record.calendarConfig.holidayDates?
                    $scope.record.calendarConfig.holidayDates = []
                $scope.record.calendarConfig.holidayDates.push($scope.createDatePickerModel(null))

            $scope.removeHoliday = (holidayDate) ->
                $scope.changeStatus.markAsChanged()
                if $scope.record.calendarConfig.holidayDates?
                    $scope.record.calendarConfig.holidayDates.splice(_.indexOf($scope.record.calendarConfig.holidayDates, holidayDate), 1)
                    $scope.removeDatePickerModel(holidayDate)

            $scope.onOpenDatePicker = -> $scope.changeStatus.markAsChanged()

            $scope.createSampleMasterData = ->
                status = $scope.loadStatus.getStatusByDataName('data')

                params = {
                    who: $scope.who
                    category: 'client'
                    action: 'create-sample-master-data'
                    clientId: $stateParams.id
                }

                CtrlUtilsService.doPost(params, status, null, null, null, 'save')

            $scope.generateVisitAndOrder = ->
                status = $scope.loadStatus.getStatusByDataName('data')

                params = {
                    who: $scope.who
                    category: 'client'
                    action: 'generate-visit-and-order'
                    clientId: $stateParams.id
                }

                CtrlUtilsService.doPost(params, status, null, null, null, 'save')

            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.init()
    ])
