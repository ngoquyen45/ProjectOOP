'use strict'

angular.module('app')

.controller('DistributorCtrl', [
        '$scope', 'CtrlCategoryInitiatorService', '$filter'
        ($scope, CtrlCategoryInitiatorService, $filter) ->
            $scope.title = 'distributor.title'
            $scope.categoryName = 'distributor'
            $scope.usePopup = false
            $scope.isUseDistributorFilter = -> false

            $scope.useActivateButton = false

            $scope.columns = [
                { header: 'name', property: 'name' }
                { header: 'code', property: 'code' }
                { header: 'supervisor', property: (record) -> record.supervisor.fullname }
            ]

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
    ])

.controller('DistributorDetailCtrl', [
        '$scope', '$stateParams', '$filter', 'CtrlUtilsService', 'LoadingUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService'
        ($scope, $stateParams, $filter, CtrlUtilsService, LoadingUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService) ->
            $scope.title = 'distributor.title'
            $scope.categoryName  = 'distributor'
            $scope.isIdRequire = -> true
            $scope.defaultBackState = 'distributor'

            # AFTER LOAD
            $scope.onLoadSuccess = ->
                if $scope.record.supervisor?
                    $scope.record.supervisorId = $scope.record.supervisor.id

            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)

            $scope.addInitFunction( ->
                LoadingUtilsService.loadSupervisors(
                    $scope.who
                    $scope.loadStatus.getStatusByDataName('supervisor')
                    (list) -> $scope.supervisors = list
                )
            )

            $scope.init()
    ])
