'use strict'

angular.module('app.directives')

.directive('nameCategoryCombobox', [
        ->
            restrict: 'E'
            replace: true
            scope:
                datas: '='
                readonlyName: '='
                placeholder: '@'
            templateUrl: 'views/directive/name-category-combobox.html'
            require: 'ngModel'
            link: (scope, element, attrs, ngModelCtrl) ->
                scope.container = {}

                scope.getPlaceholder = -> if scope.placeholder? then '-- ' + scope.placeholder + ' --' else '--'

                scope.onSelect = ($item, $model) ->
                    ngModelCtrl.$setViewValue($model)

                ngModelCtrl.$render = ->
                    scope.container.selectedId = ngModelCtrl.$viewValue

                scope.getReadonlyName = ->
                    if scope.readonlyName?
                        return scope.readonlyName

                    if scope.datas?
                        for data in scope.datas
                            return data.name if (data.id is scope.container.selectedId or !(data.id? and scope.container.selectedId))

                    return ''
    ])

.directive('nameCodeCategoryCombobox', [
        ->
            restrict: 'E'
            replace: true
            scope:
                datas: '='
                readonlyName: '='
                placeholder: '@'
            templateUrl: 'views/directive/name-code-category-combobox.html'
            require: 'ngModel'
            link: (scope, element, attrs, ngModelCtrl) ->
                scope.container = {}

                scope.getPlaceholder = -> if scope.placeholder? then '-- ' + scope.placeholder + ' --' else '--'

                scope.onSelect = ($item, $model) ->
                    ngModelCtrl.$setViewValue($model)

                ngModelCtrl.$render = ->
                    scope.container.selectedId = ngModelCtrl.$viewValue

                scope.getReadonlyName = ->
                    if scope.readonlyName?
                        return scope.readonlyName

                    if scope.datas? and scope.container.selectedId?
                        for data in scope.datas
                            return data.name if data.id is scope.container.selectedId

                    return ''

    ])

.directive('userCombobox', [
        ->
            restrict: 'E'
            replace: true
            scope:
                datas: '='
                readonlyName: '='
                placeholder: '@'
            templateUrl: 'views/directive/user-combobox.html'
            require: 'ngModel'
            link: (scope, element, attrs, ngModelCtrl) ->
                scope.container = {}

                scope.getPlaceholder = -> if scope.placeholder? then '-- ' + scope.placeholder + ' --' else '--'

                scope.onSelect = ($item, $model) ->
                    ngModelCtrl.$setViewValue($model)

                ngModelCtrl.$render = ->
                    scope.container.selectedId = ngModelCtrl.$viewValue

                scope.getReadonlyName = ->
                    if scope.readonlyName?
                        return scope.readonlyName

                    if scope.datas? and scope.container.selectedId?
                        for data in scope.datas
                            return data.fullname if data.id is scope.container.selectedId

                    return ''

    ])
