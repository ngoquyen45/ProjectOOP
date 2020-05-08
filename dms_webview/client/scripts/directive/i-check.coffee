'use strict'

angular.module('app.directives')

.controller('ICheckController', [
    '$scope'
    ($scope) ->
        ngModelCtrl = { $setViewValue: angular.noop }

        @init = (ngModelCtrl_) ->
            ngModelCtrl = ngModelCtrl_

        $scope.click = ->
            if (not $attrs?) or (not $attrs.disabled?)
                if ngModelCtrl.$viewValue
                    ngModelCtrl.$setViewValue(false)
                else
                    ngModelCtrl.$setViewValue(true)

        $scope.isChecked = ->
            ngModelCtrl.$viewValue

    ])

.directive('iCheck', ->
    restrict: 'E'
    replace: true
    scope:
        ngDisabled: '='
    require: ['iCheck', '?ngModel']
    templateUrl: 'views/directive/i-check.html'
    controller: 'ICheckController'
    link: (scope, element, attrs, controllers) ->
        iCheckCtrl = controllers[0]
        ngModelCtrl = controllers[1]

        if (not ngModelCtrl?)
            return

        iCheckCtrl.init ngModelCtrl
)
