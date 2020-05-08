'use strict'

angular.module('app')

.controller('VanSalesCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) ->
            $scope.title = 'van.sales.title'

            # LOADING
            $scope.reloadData = ->
                $scope.records = []

                params =
                    who: $scope.who
                    category: 'van-sales'

                CtrlUtilsService.loadListData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (records) ->
                        $scope.records = records

                    ->
                        toast.logError($filter('translate')('loading.error'))
                )

            $scope.isEmpty = -> _.isEmpty($scope.records)

            # SAVE
            $scope.checkBeforeSave = ->
                return true

            $scope.getObjectToSave = ->
                saveData = {}

                for record in $scope.records
                    saveData[record.id] = record.vanSales

                return saveData

            $scope.getSaveParams = (params) ->
                params.category = 'van-sales'
                return params

            $scope.onSaveSuccess = -> # DO NOTHING
            $scope.onSaveFailure = -> # DO NOTHING
            $scope.isPost = -> false

             # INIT
            CtrlInitiatorService.initCanSaveViewCtrl($scope)

            $scope.addInitFunction(->
                $scope.reloadData()
            )

            $scope.init()

    ])
