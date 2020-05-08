# GOOGLE MAP
@staticOnMapReady = ($scope, callback) ->
    $scope.$on('mapInitialized', (event, map) ->
        if jQuery.isFunction(callback) then callback(new MyMap(map))
    )

@staticAddMarkerToMap = (myMap, myMarker) ->
    if myMap? and myMarker?
        myMarker.getObject().setMap(myMap.getObject())

@staticClearMarkers = (myMap, myMarkers) ->
    if myMarkers?
        if Array.isArray(myMarkers)
            for myMarker in myMarkers
                myMarker.getObject().setMap(null)
        else
            myMarkers.getObject().setMap(null)

class @MyMap
    constructor: (@map) ->

    setCenter: (myLatLng) ->
        @map.setCenter(myLatLng.getObject())

    panTo: (myLatLng) ->
        @map.panTo(myLatLng.getObject())

    setZoom: (zoom) ->
        @map.setZoom(zoom)

    fitBounds: (myLatLngBounds) ->
        @map.fitBounds(myLatLngBounds.getObject())

    addDoubleClickListener: (callback) ->
        @map.addListener('dblclick', (event) ->
            lat = event.latLng.lat()
            lng = event.latLng.lng()
            if jQuery.isFunction(callback) then callback(new MyLatLng(lat, lng))
        )

    getObject: ->
        return @map

class @MyMarker
    constructor: (title) ->
        @marker = new google.maps.Marker(title: title)
        @marker.setAnimation(google.maps.Animation.DROP)

    getPosition: ->
        return new MyLatLng(@marker.getPosition().lat(), @marker.getPosition().lng())

    setPosition: (myLatLng) ->
        @marker.setPosition(myLatLng.getObject())

    setIcon: (icon) ->
        @marker.setIcon(icon)

    setDraggable: (draggable) ->
        @marker.setDraggable(draggable)

    addClickListener: (callback) ->
        @marker.addListener('click', (event) ->
            lat = event.latLng.lat()
            lng =  event.latLng.lng()
            if jQuery.isFunction(callback) then callback(new MyLatLng(lat, lng))
        )

    addDragEndListener: (callback) ->
        @marker.addListener('dragend', ->
            if jQuery.isFunction(callback) then callback()
        )

    getObject: ->
        return @marker

class @MyLatLng
    constructor: (@latitude, @longitude) ->
        @latLng = new google.maps.LatLng(@latitude, @longitude)

    lat: ->
        return @latitude

    lng: ->
        return @longitude

    getObject: ->
        return @latLng

class @MyLatLngBounds
    constructor: ->
        @LatLngBounds = new google.maps.LatLngBounds()

    extend: (myLatLng) ->
        @LatLngBounds.extend(myLatLng.getObject())

    getObject: ->
        return @LatLngBounds

class @MyInfoWindow
    constructor: ->
        @infoWindow = new google.maps.InfoWindow()

    setContent: (content) ->
        @infoWindow.setContent(content)

    open: (myMap, myLatLng) ->
        @infoWindow.setPosition(myLatLng.getObject())
        @infoWindow.open(myMap.getObject())

    close: ->
        @infoWindow.close()

angular.module("app").run(["$templateCache", ($templateCache) ->
    $templateCache.put('map.html', '<map class="ui-map"></map>')
])
