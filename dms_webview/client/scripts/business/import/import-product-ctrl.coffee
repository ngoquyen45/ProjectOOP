'use strict'

angular.module('app')

.controller('ImportProductCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) ->
            $scope.title = 'import.product.title'
            $scope.defaultBackState = 'product'

            initStep = ->
                $scope.currentStep = 0
                $scope.steps = [
                    { name: 'import.step.upload.file' }
                    { name: 'import.step.confirm' }
                    { name: 'import.step.add.photo' }
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
                        return ($scope.global.confirm.rowDatas isnt null and $scope.global.confirm.rowDatas.length >= $scope.global.confirm.total) or $scope.global.confirm.total <= 0
                    else if $scope.currentStep is 2
                        return not $scope.isFormValid('form')
                return true
            $scope.nextStep = ->
                if $scope.canNext()
                    if $scope.currentStep is 0
                        verify()
                    else if $scope.currentStep is 1
                        getValidRows()
                    else if $scope.currentStep is 2
                        importProduct()

            $scope.canBack = ->
                $scope.currentStep? and $scope.currentStep >= 0 and not $scope.isFinish()
            $scope.backStep = () ->
                if $scope.currentStep is 0
                    $scope.back()
                else
                    $scope.currentStep--

            $scope.isFinish = -> $scope.currentStep >= 3
            $scope.finish = -> $scope.back() if $scope.isFinish()

            $scope.getTemplateLink = () ->
                ADDRESS_BACKEND +
                $scope.who + '/import/product/template?' +
                'access_token=' + $token.getAccessToken() +
                '&lang=' + CtrlUtilsService.getUserLanguage()

            $scope.getRowErrorData = (data) ->
                if _.isEmpty(data)
                    return '<' + $filter('translate')('import.customer.data.empty') + '>'
                else
                    return data

            verify = ->
                $scope.global.confirm = {}

                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'import'
                    subCategory: 'product'
                    param: 'verify'
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.confirm = data
                    $scope.currentStep++

                CtrlUtilsService.loadSingleData(params, status, onSuccess, null)

            getValidRows = ->
                $scope.global.data = {}
                $scope.global.photos = []

                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'import'
                    subCategory: 'product'
                    param: 'confirm'
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.data = data
                    $scope.global.photos = new Array(data.rowDatas.length)
                    $scope.currentStep++

                CtrlUtilsService.loadSingleData(params, status, onSuccess, null)

            importProduct = ->
                $scope.global.result = {}

                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'import'
                    subCategory: 'product'
                    param: 'import'
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.result = data
                    $scope.currentStep++

                CtrlUtilsService.doPost(params, status, { photos : $scope.global.photos }, onSuccess, null, 'save')

            CtrlInitiatorService.initCanBackViewCtrl($scope)

            $scope.addInitFunction( ->
                initStep()

                $scope.global = {}
                $scope.global.distributorId = null
                $scope.global.distributorName = null
                $scope.global.excel = null
                $scope.global.confirm = null
            )

            $scope.init()

    ])
