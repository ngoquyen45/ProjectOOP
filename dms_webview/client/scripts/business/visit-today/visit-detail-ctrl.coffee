'use strict'

angular.module('app')

.controller('VisitDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams, ADDRESS_BACKEND) ->
            $scope.title = 'visit.detail.title'
            $scope.defaultBackState = 'visit-today'

            CtrlInitiatorService.initCanBackViewCtrl($scope)

            $scope.isClosed = -> $scope.record? and $scope.record.closed

            $scope.getTimeDisplay = ->
                if $scope.record? and $scope.record.duration? and $scope.record.startTime? and $scope.record.endTime?
                    minute = parseInt($scope.record.duration / 60000)
                    second = parseInt(($scope.record.duration / 1000) % 60)
                    duration = minute + 'm ' + second + 's'

                    time = $filter('isoTime')($scope.record.startTime) + ' - ' + $filter('isoTime')($scope.record.endTime)

                    time + ' (' + duration + ')'
                else ''

            $scope.getDurationDisplay = ->
                if $scope.record? and $scope.record.duration?
                    minute = parseInt($scope.record.duration / 60000)
                    second = parseInt(($scope.record.duration / 1000) % 60)
                    minute + 'm ' + second + 's'
                else ''

            $scope.getStartEndDisplay = ->
                if $scope.record? and $scope.record.startTime? and $scope.record.endTime?
                    $filter('isoTime')($scope.record.startTime) + ' - ' + $filter('isoTime')($scope.record.endTime)
                else ''

            $scope.isErrorDistance = -> not ($scope.record? and $scope.record.locationStatus? and $scope.record.locationStatus is 0)
            $scope.getDistanceDisplay = ->
                if $scope.record? and $scope.record.locationStatus? and ($scope.record.locationStatus is 0 or $scope.record.locationStatus is 1)
                    if $scope.record.distance? then $filter('number')($scope.record.distance * 1000, 0) + ' m' else ''
                else if $scope.record? and $scope.record.locationStatus? and $scope.record.locationStatus is 3
                    $filter('translate')('visit.customer.location.undefined')
                else
                    $filter('translate')('visit.salesman.location.undefined')

            $scope.hasOrder = -> $scope.record? and $scope.record.hasOrder
            $scope.getOrderDisplay = ->
                if $scope.hasOrder()
                    $filter('number')($scope.record.grandTotal, 0)
                else
                    $filter('translate')('visit.no.order')
            $scope.printOrder = ->  CtrlUtilsService.printOrder('order')

            $scope.hasFeedback = -> $scope.record? and $scope.record.feedbacks? and $scope.record.feedbacks.length > 0

            $scope.hasPhoto = -> $scope.record? and $scope.record.photo?

            $scope.getPhoto = ->
                if $scope.record? and $scope.record.photo?
                    return ADDRESS_BACKEND + 'image/' + $scope.record.photo

            #MAP
            staticOnMapReady($scope, (map) ->
                $scope.map = map
                addMarkerToMap()
            )

            addMarkerToMap = ->
                if $scope.map?
                    if $scope.salesmanMarker? or $scope.customerMarker?
                        staticAddMarkerToMap($scope.map, $scope.salesmanMarker) if $scope.salesmanMarker?
                        staticAddMarkerToMap($scope.map, $scope.customerMarker) if $scope.customerMarker?

                        if $scope.salesmanMarker? and $scope.customerMarker?
                            bounds = new MyLatLngBounds()
                            bounds.extend($scope.salesmanMarker.getPosition())
                            bounds.extend($scope.customerMarker.getPosition())
                            $scope.map.fitBounds(bounds)
                        else
                            $scope.map.panTo(if $scope.salesmanMarker? then $scope.salesmanMarker.getPosition() else $scope.customerMarker.getPosition())
                            $scope.map.setZoom(CtrlUtilsService.getDefaultZoom() + 2)
                    else
                        latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude)
                        $scope.map.setCenter(latLng)
                        $scope.map.setZoom(CtrlUtilsService.getDefaultZoom())

            # LOADING
            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'visit'
                    id: $stateParams.id

                CtrlUtilsService.loadSingleData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (record) ->
                        $scope.record = record

                        #MAP
                        if $scope.record.location?
                            latLng = new MyLatLng($scope.record.location.latitude, $scope.record.location.longitude)
                            $scope.salesmanMarker = new MyMarker($filter('translate')('salesman.location'))
                            $scope.salesmanMarker.setPosition(latLng)
                            $scope.salesmanMarker.setDraggable(false)
                            $scope.salesmanMarker.setIcon('images/marker-salesman.png')

                        if $scope.record.customerLocation?
                            latLng = new MyLatLng($scope.record.customerLocation.latitude, $scope.record.customerLocation.longitude)
                            $scope.customerMarker = new MyMarker($filter('translate')('customer.location'))
                            $scope.customerMarker.setPosition(latLng)
                            $scope.customerMarker.setDraggable(false)
                            $scope.customerMarker.setIcon('images/marker-customer.png')

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
                $scope.salesmanMarker = null
                $scope.customerMarker = null

                $scope.reloadData()
            )

            $scope.init()
])
