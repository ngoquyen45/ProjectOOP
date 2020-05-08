'use strict'

angular.module('app')

.controller('OrderPendingCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'order.approval.title'

            # FILTER
            $scope.filterChange = ->
                $scope.filter.currentPage = 1
                $scope.pageChange()

            $scope.goToDetail = (record) ->  $state.go('order-approval', { id: record.id, filter: $stateParams.filter })

            # LOADING
            $scope.getReloadDataParams = (params) ->
                params.category = 'order'
                params.subCategory = 'pending'
                return params

            CtrlInitiatorService.initPagingFilterViewCtrl($scope)

            $scope.addInitFunction( ->
                $scope.reloadData()
            )

            $scope.init()
    ])

.controller('OrderApprovalCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'order.approval.title'
            $scope.defaultBackState = 'order-pending'

            CtrlInitiatorService.initCanBackViewCtrl($scope)

            $scope.printOrder = ->  CtrlUtilsService.printOrder('order')

            doAction = (action, callbackSuccess, callbackFailure) ->
                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'order'
                    subCategory: 'pending'
                    id: $stateParams.id
                    action: action

                CtrlUtilsService.doPut(params, status, null, callbackSuccess, callbackFailure, action)

            $scope.approve = ->
                doAction('approve', ->
                    $state.go('order-detail', { id: $scope.record.id, parent: 'order-pending', filter: $stateParams.filter })
                )
            $scope.reject = -> doAction('reject', -> $scope.back())

            # LOADING
            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'order'
                    subCategory: 'pending'
                    id: $stateParams.id

                CtrlUtilsService.loadSingleData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (record) ->
                        $scope.record = record
                    (error) ->
                        if (error.status is 400)
                            $state.go('404')
                        else
                            toast.logError($filter('translate')('loading.error'))
                )

            $scope.addInitFunction( ->
                if not $stateParams.id?
                    $state.go('404')

                $scope.reloadData()
            )

            $scope.init()
])
