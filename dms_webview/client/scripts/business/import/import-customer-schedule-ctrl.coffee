'use strict'

angular.module('app')

.controller('ImportCustomerScheduleCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) ->
            $scope.title = 'import.customer.schedule.title'
            $scope.defaultBackState = 'customer-schedule'
            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'

            initStep = ->
                $scope.currentStep = 0
                if $scope.isUseDistributorFilter()
                    $scope.steps = [
                        { name: 'import.step.select.distributor' }
                        { name: 'import.step.upload.file' }
                        { name: 'import.step.confirm' }
                        { name: 'import.step.finalisation' }
                    ]
                else
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
                    if $scope.currentStep is ($scope.steps.length - 4)
                        return $scope.global.distributorId is null
                    else if $scope.currentStep is ($scope.steps.length - 3)
                        return $scope.global.excel is null
                    else if $scope.currentStep is ($scope.steps.length - 2)
                        return false
                return true
            $scope.nextStep = ->
                if $scope.canNext()
                    if $scope.currentStep is ($scope.steps.length - 4)
                        for distributor in $scope.distributors
                            if distributor.id is $scope.global.distributorId
                                $scope.global.distributorName = distributor.name
                        $scope.currentStep++;
                    else if $scope.currentStep is ($scope.steps.length - 3)
                        verify()
                    else if $scope.currentStep is ($scope.steps.length - 2)
                        importCustomerSchedule()

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
                $scope.who + '/import/customer-schedule/template?' +
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
                    subCategory: 'customer-schedule'
                    param: 'verify'
                    distributorId: $scope.global.distributorId
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.confirm = data
                    $scope.currentStep++

                CtrlUtilsService.loadSingleData(params, status, onSuccess, null)

            importCustomerSchedule = ->
                $scope.global.result = {}

                status = $scope.loadStatus.getStatusByDataName('data')

                params =
                    who: $scope.who
                    category: 'import'
                    subCategory: 'customer-schedule'
                    param: 'import'
                    distributorId: $scope.global.distributorId
                    fileId: $scope.global.excel.fileId

                onSuccess = (data) ->
                    $scope.global.result = data
                    $scope.currentStep++

                CtrlUtilsService.doPost(params, status, { photos : $scope.global.photos }, onSuccess, null, 'save')

            CtrlInitiatorService.initCanBackViewCtrl($scope)

            $scope.addInitFunction( ->
                if $scope.isUseDistributorFilter()
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
