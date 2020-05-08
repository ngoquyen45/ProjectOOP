'use strict'

angular.module('app.directives')

.directive('enterPressed', ->
    (scope, element, attrs) ->
        element.bind('keydown keypress', (event) ->
            if event.which is 13
                scope.$apply ->
                    scope.$eval attrs.enterPressed

                event.preventDefault()

        )
)
