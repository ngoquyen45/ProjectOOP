'use strict'

angular.module('app')

.controller('ImportMasterDataCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) ->
            $scope.title = 'Import Master Data'
            $scope.defaultBackState = 'client-detail'

            initStep = ->
                $scope.currentStep = 0
                $scope.steps = [
                    { name: 'import.step.upload.file' }
                    { name: 'import.step.confirm' }
                    { name: 'import.step.finalisation' }
                ]

            $scope.isDoneStep = (stepIndex) -> stepIndex < $scope.currentStep
            $scope.isCurrentStep = (stepIndex) -> $scope.currentStep is stepIndex

            $scope.canNext = ->
                $scope.currentStep? and $scope.currentStep >= 0 and not $scope.isFinish()
            $scope.isNextDisabled = ->
                if $scope.canNext()
                    if $scope.currentStep is 0
                        return $scope.global.excel is null
                    else if $scope.currentStep is 1
                        return not $scope.global.confirm? or $scope.global.confirm.errorTemplate

                return true
            $scope.nextStep = ->
                if $scope.canNext()
                    if $scope.currentStep is 0
                        verify()
                    else
                        importMasterData()

            $scope.canBack = ->
                $scope.currentStep? and $scope.currentStep >= 0 and not $scope.isFinish()
            $scope.backStep = () ->
                $scope.global.excel = null
                if $scope.currentStep is 0
                    $scope.back()
                else
                    $scope.currentStep--

            $scope.isFinish = -> $scope.currentStep >= 2
            $scope.finish = -> $scope.back() if $scope.isFinish()

            $scope.getTemplateLink = () ->
                ADDRESS_BACKEND +
                $scope.who + '/import/master-data/template?' +
                'access_token=' + $token.getAccessToken() +
                '&lang=' + CtrlUtilsService.getUserLanguage()

            $scope.getRowErrorData = (data) ->
                if _.isEmpty(data)
                    return '<' + $filter('translate')('import.data.empty') + '>'
                else
                    return data

            verify = ->
                $scope.global.confirm = {}

                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'import'
                    subCategory: 'master-data'
                    param: 'verify'
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.confirm = data
                    $scope.columnIndex = [0..($scope.global.confirm.nbColumn - 1)]
                    $scope.currentStep++

                CtrlUtilsService.loadSingleData(params, status, onSuccess, null)

            importMasterData = ->
                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'import'
                    subCategory: 'master-data'
                    param: 'import'
                    clientId: $stateParams.id
                    fileId: $scope.global.excel.fileId

                onSuccess = ->
                    $scope.currentStep++

                CtrlUtilsService.doPost(params, status, null, onSuccess, null, 'save')

            CtrlInitiatorService.initCanBackViewCtrl($scope)
            $scope.back = ->
                backState = $stateParams.parent ? $scope.defaultBackState
                $state.go( backState, { id: $stateParams.id, filter: $stateParams.filter } )

            $scope.addInitFunction( ->
                if not $stateParams.id?
                    $state.go('404')

                initStep()

                $scope.global = {}
                $scope.global.excel = null
                $scope.global.confirm = null
            )

            $scope.init()

    ])
