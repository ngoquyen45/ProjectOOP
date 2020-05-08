'use strict'

angular.module('app')

.controller('DashboardCtrl', [
    '$scope', '$state', '$stateParams', 'toast', '$filter', 'CtrlUtilsService', 'CtrlInitiatorService'
    ($scope, $state, $stateParams, toast, $filter, CtrlUtilsService, CtrlInitiatorService) ->

        CtrlInitiatorService.initBasicViewCtrl($scope)

        $scope.getComparisonIcon = (before, current) ->
            before ?= 0
            current ?= 0

            if (before < current)
                return 'fa-arrow-circle-up'
            else if (before > current)
                return 'fa-arrow-circle-down'
            else if (before is current)
                return 'fa-arrow-circle-right'

        $scope.isDisplayProgressWarning = ->
            $scope.record? and $scope.record.progressWarnings? and $scope.record.progressWarnings.length > 0

        $scope.isDisplayBestSellers = ->
            $scope.record? and $scope.record.bestSellers? and $scope.record.bestSellers.length > 0

        $scope.reloadData = ->
            status = $scope.loadStatus.getStatusByDataName('data')

            params =
                who: $scope.who
                category: 'dashboard'

            callbackSuccess = (data) ->
                $scope.record = data

            callbackFailure = ->
                toast.logError($filter('translate')('loading.error'))

            CtrlUtilsService.loadSingleData(params, status, callbackSuccess, callbackFailure)

        $scope.addInitFunction( -> $scope.reloadData())

        $scope.init()
])
