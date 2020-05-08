'use strict'

angular.module('app')

.controller('VisitTodayMapCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state) ->
            $scope.title = 'visit.today.title'
            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'
            $scope.mustSelectDistributor = -> $scope.isUseDistributorFilter() and not $scope.filter.distributorId?

            $scope.afterFilterLoad = -> # DO NOTHING

            clearMarkers = ->
                if $scope.markers?
                    staticClearMarkers($scope.map, $scope.markers)

                    $scope.markers = null

                    if $scope.map?
                        latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude)
                        $scope.map.setCenter(latLng)
                        $scope.map.setZoom(CtrlUtilsService.getDefaultZoom())

            showInfoWindow = (latLng) ->
                if $scope.markers?
                    for marker, i in $scope.markers
                        if marker.getPosition().lat()is latLng.lat() and marker.getPosition().lng() is latLng.lng()
                            content = '<strong> ' + $scope.records[i].name + ' </strong><br/>'
                            if $scope.records[i].visitInfo?
                                detailLink = '#/visit-detail?' +
                                    'id=' + $scope.records[i].visitInfo.id + '&' +
                                    'parent=visit-today-map&' +
                                    'filter=' + $scope.getFilterAsString()

                                content = content + '<a class="underline-link" href="' + detailLink + '">' +
                                        $filter('translate')('visit.today.view.detail') + '</a>'
                            else
                                content = content + $filter('translate')('visit.today.not.visited')

                            $scope.infowindow.setContent(content)
                            $scope.infowindow.open($scope.map, marker.getPosition())
                            return

                $scope.infowindow.close()

            addMarkersToMap = ->
                if $scope.map?
                    if $scope.records? and $scope.records.length > 0
                        $scope.markers = []
                        $scope.infowindow = new MyInfoWindow()

                        bounds = new MyLatLngBounds()

                        for record, i in $scope.records
                            latLng = new MyLatLng(record.location.latitude, record.location.longitude)
                            bounds.extend(latLng)

                            marker = new MyMarker($filter('translate')('location'))
                            staticAddMarkerToMap($scope.map, marker)
                            marker.setPosition(latLng)
                            marker.setDraggable(false)
                            if record.visitInfo?
                                marker.setIcon('images/marker-ok.png')
                            else
                                marker.setIcon('images/marker-warning.png')


                            marker.addClickListener((latLng) -> showInfoWindow(latLng))

                            $scope.markers.push(marker)

                        $scope.map.fitBounds(bounds)
                    else
                        latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude)
                        $scope.map.setCenter(latLng)
                        $scope.map.setZoom(CtrlUtilsService.getDefaultZoom())

            staticOnMapReady($scope, (map) ->
                $scope.map = map
                addMarkersToMap()
            )

            # FILTER
            $scope.distributorFilterChange = ->
                $scope.filter.salesmanId = null
                $scope.filterChange()

            $scope.filterChange = ->
                $scope.reloadForNewFilter()

            # LOADING
            $scope.reloadData = ->
                if not $scope.mustSelectDistributor()
                    LoadingUtilsService.loadSalesmenByDistributor(
                        $scope.who
                        $scope.filter.distributorId
                        $scope.loadStatus.getStatusByDataName('salesmen')
                        (list) -> $scope.salesmen = list
                    )

                    if $scope.filter.salesmanId?
                        clearMarkers()

                        params =
                            who: $scope.who
                            category: 'visit'
                            subCategory: 'today'
                            param: 'by-salesman'
                            salesmanId: $scope.filter.salesmanId

                        CtrlUtilsService.loadListData(params
                            $scope.loadStatus.getStatusByDataName('data')
                            (records, count) ->
                                $scope.records = records
                                $scope.count = count
                                addMarkersToMap()
                            (error) ->
                                if (error.status is 400)
                                    $state.go('404')
                                else
                                    toast.logError($filter('translate')('loading.error'))
                        )

            CtrlInitiatorService.initFilterViewCtrl($scope)

            $scope.addInitFunction( ->
                $scope.map = null
                $scope.markers = null

                if $scope.isUseDistributorFilter()
                    LoadingUtilsService.loadDistributors(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('distributors')
                        (list) -> $scope.distributors = list
                    )

                $scope.reloadData()
            )

            $scope.init()
])
