'use strict'

angular.module('app')

.controller('VisitTodayCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'visit.today.title'
            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'
            $scope.mustSelectDistributor = -> $scope.isUseDistributorFilter() and not $scope.filter.distributorId?

            $scope.getDurationDisplay = (duration) ->
                if duration?
                    minute = parseInt(duration / 60000)
                    second = parseInt((duration / 1000) % 60)
                    return minute + 'm ' + second + 's'
                else return ''

            $scope.getStartEndDisplay = (record) ->
                if record? and record.startTime? and record.endTime?
                    return $filter('isoTime')(record.startTime) + ' - ' + $filter('isoTime')(record.endTime)
                else return ''

            $scope.isDistanceNormal = (record) -> record.locationStatus? and record.locationStatus is 0
            $scope.isDistanceTooFar = (record) -> record.locationStatus? and record.locationStatus is 1
            $scope.isLocationSalesmanUndefined = (record) -> record.locationStatus? and record.locationStatus is 2
            $scope.isLocationCustomerUndefined = (record) -> record.locationStatus? and record.locationStatus is 3
            $scope.getDistanceDisplay = (distance) -> if distance? then $filter('number')(distance * 1000, 0) + ' m' else ''

            $scope.isNoOrder = (record) -> not (record.hasOrder? and record.hasOrder)
            $scope.isOrderApproved = (record) -> not $scope.isNoOrder(record) and record.approveStatus is 1
            $scope.isOrderPending = (record) -> not $scope.isNoOrder(record) and record.approveStatus is 0
            $scope.isOrderRejected = (record) -> not $scope.isNoOrder(record) and record.approveStatus is 2

            $scope.goToDetail = (record) ->  $state.go('visit-detail', { id: record.id, filter: $stateParams.filter })

            # FILTER
            $scope.distributorFilterChange = ->
                $scope.filter.salesmanId = null
                $scope.filterChange()

            $scope.filterChange = ->
                $scope.filter.currentPage = 1
                $scope.pageChange()

            # LOADING
            $scope.getReloadDataParams = (params) ->
                if not $scope.mustSelectDistributor()
                    LoadingUtilsService.loadSalesmenByDistributor(
                        $scope.who
                        $scope.filter.distributorId
                        $scope.loadStatus.getStatusByDataName('salesmen')
                        (list) ->
                            $scope.salesmen = _.union(
                                [
                                    { id: 'all', fullname: '-- ' + $filter('translate')('all') + ' --'  }
                                ]
                                list
                            )
                    )

                    params.category = 'visit'
                    params.subCategory = 'today'

                    if $scope.isUseDistributorFilter()
                        params.distributorId = $scope.filter.distributorId

                    if $scope.filter.salesmanId? and $scope.filter.salesmanId isnt 'all'
                        params.salesmanId = $scope.filter.salesmanId

                    # Load Summary
                    summaryParams = angular.copy(params)
                    summaryParams.param = 'summary'
                    summaryLoadStatus = $scope.loadStatus.getStatusByDataName('summary')
                    $scope.summary = {}
                    summaryCallback = (data) -> $scope.summary = data
                    CtrlUtilsService.loadSingleData(summaryParams, summaryLoadStatus, summaryCallback, null)

                    return params

            CtrlInitiatorService.initPagingFilterViewCtrl($scope)

            $scope.addInitFunction( ->
                $scope.filter.salesmanId = $scope.filter.salesmanId ? 'all'

                if $scope.isUseDistributorFilter()
                    LoadingUtilsService.loadDistributors(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('distributors')
                        (list) -> $scope.distributors = list
                    )

                $scope.reloadData()
            )

            $scope.init()

])
