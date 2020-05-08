'use strict'

angular.module('app')

.controller('ImportDistributorPriceListCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) ->
            $scope.title = 'import.distributor.price.list.title'
            $scope.defaultBackState = 'distributor-price-list'

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
                    if $scope.currentStep is ($scope.steps.length - 3)
                        return $scope.global.excel is null
                    else if $scope.currentStep is ($scope.steps.length - 2)
                        return false
                return true
            $scope.nextStep = ->
                if $scope.canNext()
                    if $scope.currentStep is ($scope.steps.length - 3)
                        verify()
                    else if $scope.currentStep is ($scope.steps.length - 2)
                        doImport()

            $scope.canBack = ->
                $scope.currentStep? and $scope.currentStep >= 0 and not $scope.isFinish()
            $scope.backStep = () ->
                if $scope.currentStep is 0
                    $scope.back()
                else
                    $scope.currentStep--

            $scope.isFinish = -> $scope.currentStep >= ($scope.steps.length - 1)
            $scope.finish = -> $scope.back() if $scope.isFinish()

            $scope.getTemplateLink = () ->
                ADDRESS_BACKEND +
                $scope.who + '/import/price-list/template?' +
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
                    subCategory: 'price-list'
                    param: 'verify'
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.confirm = data
                    $scope.currentStep++

                CtrlUtilsService.loadSingleData(params, status, onSuccess, null)

            doImport = ->
                $scope.global.result = {}

                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'import'
                    subCategory: 'price-list'
                    param: 'import'
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.result = data
                    $scope.currentStep++

                CtrlUtilsService.doPost(params, status, null, onSuccess, null, 'save')

            CtrlInitiatorService.initCanBackViewCtrl($scope)

            $scope.addInitFunction( ->
                initStep()

                $scope.global = {}
                $scope.global.excel = null
                $scope.global.confirm = null
                $scope.global.result = null
            )

            $scope.init()

    ])
