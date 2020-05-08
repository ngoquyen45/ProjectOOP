'use strict'

angular.module('app.directives')

.controller('SearchBoxController', [
    '$scope', '$attrs'
    ($scope) ->
        ngModelCtrl = { $setViewValue: angular.noop }
        lastText = null

        @init = (ngModelCtrl_) ->
            ngModelCtrl = ngModelCtrl_
            $scope.searchText = null

        listener = $scope.$watch(ngModelCtrl.$viewValue, ->
            $scope.searchText = ngModelCtrl.$viewValue
            lastText = $scope.searchText
            listener()
        )

        $scope.textChanged = ->
            if lastText? and lastText.length > 0 and $scope.searchText? and $scope.searchText.length > 0 and lastText == $scope.searchText
                false
            else
                true

        $scope.performSearch = ->
            listener()
            ngModelCtrl.$setViewValue $scope.searchText
            ngModelCtrl.$render()
            lastText = $scope.searchText

        $scope.clearText = ->
            listener()
            lastText = ''
            $scope.searchText = ''
            ngModelCtrl.$setViewValue($scope.searchText)
            ngModelCtrl.$render()

        $scope.onEnter = ->
            if $scope.textChanged()
                $scope.performSearch()
            else
                $scope.clearText()

        $scope.onBlur = ->
            if $scope.textChanged()
                if (lastText? and lastText.length > 0) or ($scope.searchText? and $scope.searchText.length > 0)
                    $scope.performSearch()

])

.directive('searchBox', ->
    restrict: 'E'
    replace: true
    scope:
        placeholder: '@'
        ngDisabled: '='
    require: ['searchBox', '?ngModel']
    templateUrl: 'views/directive/search-box.html'
    controller: 'SearchBoxController'
    link: (scope, element, attrs, controllers) ->
        searchBoxCtrl = controllers[0]
        ngModelCtrl = controllers[1]

        if (not ngModelCtrl?)
            return

        searchBoxCtrl.init ngModelCtrl
)
