angular.module('app')

.controller('SystemActionCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService'
        ($scope, CtrlUtilsService, CtrlInitiatorService) ->
            $scope.title = 'system.action.title'

            CtrlInitiatorService.initBasicViewCtrl($scope)

            $scope.resetCache = ->
                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'system'
                    action: 'reset-cache'

                CtrlUtilsService.doPut(params, status, null, null, null, 'reset.cache')

            $scope.init()
    ])
