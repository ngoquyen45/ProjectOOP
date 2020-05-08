'use strict'

angular.module('app')

.controller('ClientConfigCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService) ->
            $scope.title = 'client.config.title'
            $scope.categoryName  = 'client-config'
            $scope.isIdRequire = -> false

            # AFTER LOAD
            $scope.onLoadSuccess = ->
                if $scope.record.visitDurationKPI?
                    $scope.record.visitDurationKPIinMinute = $scope.record.visitDurationKPI / 60 / 1000

            # GET OBJECT TO SAVE
            $scope.getObjectToSave = ->
                record = angular.copy($scope.record)
                record.visitDurationKPI = $scope.record.visitDurationKPIinMinute * 60 * 1000
                console.log(record)
                return record

            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)

            $scope.init()
    ])

