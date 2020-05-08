'use strict'

angular.module('app.directives')

.controller('locationSelectorCtrl', [
        '$scope', '$rootScope', 'CtrlUtilsService', '$filter'
        ($scope, $rootScope, CtrlUtilsService, $filter) ->
            ngModelCtrl = { $setViewValue: angular.noop }

            $scope.location = null
            $scope.map = null
            $scope.marker = null

            @init = (ngModelCtrl_) ->
                ngModelCtrl = ngModelCtrl_

                # notify that i'm ready
                $rootScope.$broadcast('locationSelectorReady', $scope.elementId )

            # LISTER ON MAP INIT
            staticOnMapReady($scope, (map) ->
                $scope.map = map

                if CtrlUtilsService.getUserInfo().location?
                    latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude)
                    $scope.map.setCenter(latLng)
                    $scope.map.setZoom(CtrlUtilsService.getDefaultZoom())

                # LOAD RECORD DONE BEFORE THEN ADD TO MAP
                if $scope.location?
                    latLng = new MyLatLng($scope.location.latitude, $scope.location.longitude)
                    createMarker(latLng)

                $scope.map.addDoubleClickListener((myLatLng) ->
                    ngModelCtrl.$setViewValue({ latitude: myLatLng.lat(), longitude: myLatLng.lng() })
                    ngModelCtrl.$render()
                    refresh()
                )
            )

            # LISTER ON MODEL UPDATE
            $scope.$on('refreshLocationSelector', (event, dto) ->

                if (not dto.id? or dto.id is $scope.elementId)
                    $scope.location = dto.location
#                    $scope.$apply()

                    if $scope.map?
                        if $scope.location?
                            latLng = new MyLatLng($scope.location.latitude, $scope.location.longitude)
                            createMarker(latLng)
                        else
                            clearMarker()

            )

            refresh = ->
                $scope.location = ngModelCtrl.$viewValue
                $scope.$apply()

                if $scope.map?
                    if $scope.location?
                        latLng = new MyLatLng($scope.location.latitude, $scope.location.longitude)
                        createMarker(latLng)
                    else
                        clearMarker()

            createMarker = (latLng) ->
                if $scope.map?
                    clearMarker()

                    markerTitle = $scope.markerTitle ? 'location'

                    $scope.marker = new MyMarker($filter('translate')(markerTitle))
                    $scope.marker.setPosition(latLng)
                    $scope.marker.setDraggable(true)
                    staticAddMarkerToMap($scope.map, $scope.marker)
                    $scope.map.panTo($scope.marker.getPosition())
                    $scope.map.setZoom(CtrlUtilsService.getDefaultZoom() + 1)

                    $scope.marker.addDragEndListener( ->
                        ngModelCtrl.$setViewValue({ latitude: $scope.marker.getPosition().lat(), longitude: $scope.marker.getPosition().lng() })
                        ngModelCtrl.$render()
                        refresh()
                    )

            clearMarker = ->
                if $scope.marker?
                    staticClearMarkers($scope.map, $scope.marker)
                    $scope.marker = null
                    if $scope.map?
                        if CtrlUtilsService.getUserInfo().location?
                            latLng = new MyLatLng(CtrlUtilsService.getUserInfo().location.latitude, CtrlUtilsService.getUserInfo().location.longitude)
                            $scope.map.setCenter(latLng)
                            $scope.map.setZoom(CtrlUtilsService.getDefaultZoom())
    ])

.directive('locationSelector', [
        ->
            restrict: 'E'
            scope:
                markerTitle: '='
            replace: true
            require: ['locationSelector', '?ngModel']
            templateUrl: 'views/directive/location-selector.html'
            controller: 'locationSelectorCtrl'
            link: (scope, element, attrs, controllers) ->
                myCtrl = controllers[0]
                ngModelCtrl = controllers[1]

                scope.elementId = $(element).attr('id')

                if ngModelCtrl?
                    myCtrl.init(ngModelCtrl)
    ])
