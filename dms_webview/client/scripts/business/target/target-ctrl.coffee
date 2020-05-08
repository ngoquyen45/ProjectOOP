'use strict'

angular.module('app')

.controller('TargetCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) ->
            $scope.title = 'target.title'

            $scope.afterFilterLoad = ->

            # FILTER
            $scope.dateChange = ->
                $scope.filter.date = globalUtils.createIsoDate($scope.date.date)
                $scope.reloadForNewFilter()

            $scope.goToDetail = (record) -> $state.go('target-detail', { id: record.salesman.id, filter: $stateParams.filter })

            # LOADING
            CtrlInitiatorService.initFilterViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'target'
                    month: $scope.date.date.getMonth()
                    year: $scope.date.date.getFullYear()

                CtrlUtilsService.loadListData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (records) -> $scope.records = records
                    (error) ->
                        if (error.status is 400)
                            $state.go('404')
                        else
                            toast.logError($filter('translate')('loading.error'))
                )

            $scope.addInitFunction( ->
                if $scope.filter.date?
                    $scope.date = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.date))
                else
                    $scope.date = $scope.createDatePickerModel(new Date())

                $scope.reloadData()
            )

            $scope.init()
    ])

.controller('TargetDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) ->
            $scope.title = 'target.title'
            $scope.defaultBackState = 'target'

            $scope.afterFilterLoad = ->

            # SAVE
            $scope.checkBeforeSave = ->
                if $scope.isFormValid('form')
                    return true
                else
                    toast.logError($filter('translate')('error.data.input.not.valid'))
                    return false

            $scope.getObjectToSave = ->
                recordForSave = angular.copy($scope.record)
                recordForSave.salesmanId = $stateParams.id
                recordForSave.month = $scope.date.getMonth()
                recordForSave.year = $scope.date.getFullYear()

                return recordForSave

            $scope.getSaveParams = (params) ->
                params.category = 'target'
                return params

            $scope.onSaveSuccess = -> $scope.refresh()
            $scope.onSaveFailure = -> # DO NOTHING
            $scope.isPost = -> false

            CtrlInitiatorService.initFilterViewCtrl($scope)
            CtrlInitiatorService.initCanBackViewCtrl($scope)
            CtrlInitiatorService.initCanSaveViewCtrl($scope)

            $scope.getMonthDisplay = -> moment($scope.date).format('MM/YYYY')

            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'target'
                    subCategory: 'detail'
                    month: $scope.date.getMonth()
                    year: $scope.date.getFullYear()
                    salesmanId: $stateParams.id

                CtrlUtilsService.loadSingleData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (record) -> $scope.record = record
                    (error) ->
                        if (error.status is 400)
                            $state.go('404')
                        else
                            toast.logError($filter('translate')('loading.error'))
                )

            $scope.addInitFunction( ->
                if $stateParams.id?
                    $scope.date = if $scope.filter.date? then globalUtils.parseIsoDate($scope.filter.date) else new Date()

                    if $scope.date?
                        $scope.reloadData()
                    else
                        $state.go('404')
                else
                    $state.go('404')
            )

            $scope.init()
    ])

