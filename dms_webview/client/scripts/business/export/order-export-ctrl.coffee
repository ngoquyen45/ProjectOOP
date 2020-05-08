'use strict'

angular.module('app')

.controller('OrderExportCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) ->
            $scope.title = 'order.export.title'
            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'

            $scope.export = ->
                if $scope.filter.distributorId? or not $scope.isUseDistributorFilter()
                    if checkDate()
                        if checkDateDuration()
                            $scope.filter.fromDate = globalUtils.createIsoDate($scope.fromDate.date)
                            $scope.filter.toDate = globalUtils.createIsoDate($scope.toDate.date)

                            href = ADDRESS_BACKEND + $scope.who + '/export/order'
                            if $scope.filter.isDetail
                                href = href + '/detail'

                            href = href + '?access_token=' + $token.getAccessToken()
                            href = href + '&distributorId=' +  $scope.filter.distributorId
                            href = href + '&fromDate=' +  $scope.filter.fromDate
                            href = href + '&toDate=' +  $scope.filter.toDate
                            href = href + '&lang=' + CtrlUtilsService.getUserLanguage()

                            location.href = href

                        else
                            toast.logError($filter('translate')('max.duration.between.from.date.and.to.date.is.2.month'))
                    else
                        toast.logError($filter('translate')('from.date.cannot.be.greater.than.to.date'))
                else
                    toast.logError($filter('translate')('please.select.distributor'))

            checkDate = ->
                if $scope.fromDate? and  $scope.fromDate.date? and $scope.toDate and $scope.toDate.date?
                    if $scope.fromDate.date.getTime() <= $scope.toDate.date.getTime()
                        return true
                return false

            checkDateDuration = ->
                if moment($scope.fromDate.date).add(2, 'months').toDate().getTime() < $scope.toDate.date.getTime()
                    return false
                return true

            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.addInitFunction( ->
                $scope.filter = {}
                $scope.filter.isDetail = false
                $scope.filter.distributorId = null
                $scope.fromDate = $scope.createDatePickerModel(new Date())
                $scope.toDate = $scope.createDatePickerModel(new Date())

                if $scope.isUseDistributorFilter()
                    LoadingUtilsService.loadDistributors(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('distributors')
                        (list) -> $scope.distributors = list
                    )
            )

            $scope.init()
    ])
