'use strict'

angular.module('app')

.controller('CustomerPendingCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'customer.approval.title'

            # FILTER
            $scope.filterChange = ->
                $scope.filter.currentPage = 1
                $scope.pageChange()

            $scope.goToDetail = (record) ->  $state.go('customer-approval', { id: record.id, filter: $stateParams.filter })

            # LOADING
            $scope.getReloadDataParams = (params) ->
                params.category = 'customer'
                params.subCategory = 'pending'
                return params

            CtrlInitiatorService.initPagingFilterViewCtrl($scope)

            $scope.addInitFunction( ->
                $scope.reloadData()
            )

            $scope.init()
    ])

.controller('CustomerApprovalCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'customer.approval.title'
            $scope.defaultBackState = 'customer-pending'

            CtrlInitiatorService.initCanBackViewCtrl($scope)

            doAction = (action, callbackSuccess, callbackFailure) ->
                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'customer'
                    subCategory: 'pending'
                    id: $stateParams.id
                    action: action

                CtrlUtilsService.doPut(params, status, null, callbackSuccess, callbackFailure, action)

            $scope.approve = ->
                doAction('approve', ->
                    $state.go('customer-schedule-single', { id: $stateParams.id } )
                )
            $scope.reject = -> doAction('reject', -> $scope.back())

            #MAP
            staticOnMapReady($scope, (map) ->
                $scope.map = map
                addMarkerToMap()
            )

            addMarkerToMap = ->
                if $scope.map?
                    if $scope.marker?
                        staticAddMarkerToMap($scope.map, $scope.marker)
                        $scope.map.panTo($scope.marker.getPosition())
                        $scope.map.setZoom(CtrlUtilsService.getDefaultZoom() + 1)
                    else
                        latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude)
                        $scope.map.setCenter(latLng)
                        $scope.map.setZoom(CtrlUtilsService.getDefaultZoom())

            # LOADING
            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'customer'
                    subCategory: 'pending'
                    id: $stateParams.id

                CtrlUtilsService.loadSingleData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (record) ->
                        $scope.record = record

                        if $scope.record.location?
                            latLng = new MyLatLng($scope.record.location.latitude, $scope.record.location.longitude)
                            $scope.marker = new MyMarker($filter('translate')('customer.location'))
                            $scope.marker.setPosition(latLng)
                            $scope.marker.setDraggable(false)
                            $scope.marker.setIcon('images/marker-customer.png')

                        addMarkerToMap()
                    (error) ->
                        if (error.status is 400)
                            $state.go('404')
                        else
                            toast.logError($filter('translate')('loading.error'))
                )

            $scope.addInitFunction( ->
                if not $stateParams.id?
                    $state.go('404')

                $scope.map = null
                $scope.marker = null

                $scope.reloadData()
            )

            $scope.init()

    ])
