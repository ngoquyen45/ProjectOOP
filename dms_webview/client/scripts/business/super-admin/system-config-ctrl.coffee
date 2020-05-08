'use strict'

angular.module('app')

.controller('SystemConfigCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', '$filter'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, $filter) ->
            $scope.title = 'system.config.title'
            $scope.categoryName  = 'system-config'
            $scope.isIdRequire = -> false

            $scope.useMap = true
            $scope.getLocation = -> $scope.record.location

            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)

            $scope.orderDateTypes = [
                { id: 'CREATED_DATE', name: $filter('translate')('order.created.date') }
                { id: 'APPROVED_DATE', name: $filter('translate')('order.approved.date') }
            ]

            $scope.init()
    ])

