'use strict'

angular.module('app')

.controller('VisitCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'visit'
            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'

            # FILTER
            $scope.search = ->
                if checkDate()
                    if checkDateDuration()
                        $scope.filter.currentPage = 1
                        $scope.filter.fromDate = globalUtils.createIsoDate($scope.fromDate.date)
                        $scope.filter.toDate = globalUtils.createIsoDate($scope.toDate.date)
                        $scope.pageChange()
                    else
                        toast.logError($filter('translate')('max.duration.between.from.date.and.to.date.is.2.month'))
                else
                    toast.logError($filter('translate')('from.date.cannot.be.greater.than.to.date'))

            checkDateDuration = ->
                if moment($scope.fromDate.date).add(2, 'months').toDate().getTime() < $scope.toDate.date.getTime()
                    return false
                return true

            checkDate = ->
                if $scope.fromDate? and  $scope.fromDate.date? and $scope.toDate and $scope.toDate.date?
                    if $scope.fromDate.date.getTime() <= $scope.toDate.date.getTime()
                        return true
                return false

            $scope.goToDetail = (record) ->  $state.go('visit-detail', { id: record.id, parent: 'visit',filter: $stateParams.filter })

            $scope.changeDistributor = ->
                if $scope.isUseDistributorFilter()
                    $scope.filter.salesmanId = 'all'
                    if $scope.isDisplaySalesmanFilter()
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

            $scope.isDisplaySalesmanFilter = ->
                not $scope.isUseDistributorFilter() or ($scope.filter.distributorId? and $scope.filter.distributorId isnt 'all')

            $scope.getDurationDisplay = (duration) ->
                if duration?
                    minute = parseInt(duration / 60000)
                    second = parseInt((duration / 1000) % 60)
                    minute + 'm ' + second + 's'
                else ''

            $scope.getStartEndDisplay = (record) ->
                if record? and record.startTime? and record.endTime?
                    $filter('isoTime')(record.startTime) + ' - ' + $filter('isoTime')(record.endTime)
                else ''

            $scope.isDistanceNormal = (record) -> record.locationStatus? and record.locationStatus is 0
            $scope.isDistanceTooFar = (record) -> record.locationStatus? and record.locationStatus is 1
            $scope.isLocationSalesmanUndefined = (record) -> record.locationStatus? and record.locationStatus is 2
            $scope.isLocationCustomerUndefined = (record) -> record.locationStatus? and record.locationStatus is 3
            $scope.getDistanceDisplay = (distance) -> if distance? then $filter('number')(distance * 1000, 0) + ' m' else ''

            $scope.isNoOrder = (record) -> not (record.hasOrder? and record.hasOrder)
            $scope.isOrderApproved = (record) -> not $scope.isNoOrder(record) and record.approveStatus is 1
            $scope.isOrderPending = (record) -> not $scope.isNoOrder(record) and record.approveStatus is 0
            $scope.isOrderRejected = (record) -> not $scope.isNoOrder(record) and record.approveStatus is 2

            # LOADING
            $scope.getReloadDataParams = (params) ->
                params.category = 'visit'
                params.fromDate = globalUtils.createIsoDate($scope.fromDate.date)
                params.toDate = globalUtils.createIsoDate($scope.toDate.date)

                if $scope.isUseDistributorFilter() and $scope.filter.distributorId isnt 'all'
                    params.distributorId = $scope.filter.distributorId

                if $scope.filter.salesmanId? and $scope.filter.salesmanId isnt 'all'
                    params.salesmanId = $scope.filter.salesmanId

                return params

            CtrlInitiatorService.initPagingFilterViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.addInitFunction( ->
                if $scope.filter.fromDate? and $scope.filter.toDate?
                    $scope.fromDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.fromDate))
                    $scope.toDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.toDate))
                    if not checkDate() or not checkDateDuration()
                        $state.go('404')
                else
                    $scope.fromDate = { date: new Date(), opened: false }
                    $scope.toDate = { date: new Date(), opened: false }

                $scope.filter.distributorId = $scope.filter.distributorId ? 'all'
                $scope.filter.salesmanId = $scope.filter.salesmanId ? 'all'

                if $scope.isUseDistributorFilter()
                    LoadingUtilsService.loadDistributors(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('distributors')
                        (list) ->
                            $scope.distributors = _.union(
                                [
                                    { id: 'all', name: '-- ' + $filter('translate')('all') + ' --'  }
                                ]
                                list
                            )
                    )

                if $scope.isDisplaySalesmanFilter()
                    distributorId = if $scope.filter.distributorId isnt 'all' then $scope.filter.distributorId else null

                    LoadingUtilsService.loadSalesmenByDistributor(
                        $scope.who
                        distributorId
                        $scope.loadStatus.getStatusByDataName('salesmen')
                        (list) ->
                            $scope.salesmen = _.union(
                                [
                                    { id: 'all', fullname: '-- ' + $filter('translate')('all') + ' --'  }
                                ]
                                list
                            )
                    )

                $scope.reloadData()
            )

            $scope.init()
])
