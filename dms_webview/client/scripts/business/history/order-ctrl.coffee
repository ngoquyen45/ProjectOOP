'use strict'

angular.module('app')

.controller('OrderCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'order'
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

            $scope.goToDetail = (record) ->  $state.go('order-detail', { id: record.id, filter: $stateParams.filter })

            checkDateDuration = ->
                if moment($scope.fromDate.date).add(2, 'months').toDate().getTime() < $scope.toDate.date.getTime()
                    return false
                return true

            checkDate = ->
                if $scope.fromDate? and  $scope.fromDate.date? and $scope.toDate and $scope.toDate.date?
                    if $scope.fromDate.date.getTime() <= $scope.toDate.date.getTime()
                        return true
                return false

            # LOADING
            $scope.getReloadDataParams = (params) ->
                params.category = 'order'
                params.fromDate = globalUtils.createIsoDate($scope.fromDate.date)
                params.toDate = globalUtils.createIsoDate($scope.toDate.date)

                if $scope.isUseDistributorFilter() and $scope.filter.distributorId isnt 'all'
                    params.distributorId = $scope.filter.distributorId

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
                    $scope.fromDate = $scope.createDatePickerModel(new Date())
                    $scope.toDate = $scope.createDatePickerModel(new Date())

                if $scope.isUseDistributorFilter()
                    LoadingUtilsService.loadDistributors(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('distributors')
                        (list) ->
                            $scope.distributors = _.union(
                                [
                                    { id: null, name: '-- ' + $filter('translate')('all') + ' --'  }
                                ]
                                list
                            )
                    )

                $scope.reloadData()
            )

            $scope.init()
])

.controller('OrderDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'order.approval.title'
            $scope.defaultBackState = 'order'

            CtrlInitiatorService.initCanBackViewCtrl($scope)

            $scope.printOrder = ->  CtrlUtilsService.printOrder('order')

            # LOADING
            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'order'
                    id: $stateParams.id

                CtrlUtilsService.loadSingleData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (record) ->
                        $scope.record = record
                    (error) ->
                        if (error.status is 400)
                            $state.go('404')
                        else
                            toast.logError($filter('translate')('loading.error'))
                )

            $scope.addInitFunction( ->
                if not $stateParams.id?
                    $state.go('404')

                $scope.reloadData()
            )

            $scope.init()
    ])

