'use strict'

angular.module('app')

.controller('ImportCustomerCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) ->
            $scope.title = 'import.customer.title'
            $scope.defaultBackState = 'customer'

            initStep = ->
                $scope.currentStep = 0
                $scope.steps = [
                    { name: 'import.step.select.distributor' }
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
                        return $scope.global.distributorId is null
                    else if $scope.currentStep is 1
                        return $scope.global.excel is null
                    else if $scope.currentStep is 2
                        return false
                return true
            $scope.nextStep = ->
                if $scope.canNext()
                    if $scope.currentStep is 0
                        for distributor in $scope.distributors
                            if distributor.id is $scope.global.distributorId
                                $scope.global.distributorName = distributor.name
                        $scope.currentStep++;
                    else if $scope.currentStep is 1
                        verify()
                    else if $scope.currentStep is 2
                        importCustomer()

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
                $scope.who + '/import/customer/template?' +
                'distributorId=' + $scope.global.distributorId +
                '&access_token=' + $token.getAccessToken() +
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
                    subCategory: 'customer'
                    param: 'verify'
                    distributorId: $scope.global.distributorId
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.confirm = data
                    $scope.currentStep++

                CtrlUtilsService.loadSingleData(params, status, onSuccess, null)

            importCustomer = ->
                $scope.global.result = {}

                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'import'
                    subCategory: 'customer'
                    param: 'import'
                    distributorId: $scope.global.distributorId
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.result = data
                    $scope.currentStep++

                CtrlUtilsService.doPost(params, status, { photos : $scope.global.photos }, onSuccess, null, 'save')

            CtrlInitiatorService.initCanBackViewCtrl($scope)

            $scope.addInitFunction( ->
                LoadingUtilsService.loadDistributors(
                    $scope.who
                    $scope.loadStatus.getStatusByDataName('distributors')
                    (list) -> $scope.distributors = angular.copy(list)
                )

                initStep()

                $scope.global = {}
                $scope.global.distributorId = null
                $scope.global.distributorName = null
                $scope.global.excel = null
                $scope.global.confirm = null
                $scope.global.result = null
            )

            $scope.init()

    ])
